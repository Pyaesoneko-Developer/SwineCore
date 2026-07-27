package com.example.swinecore.service;

import com.example.swinecore.entity.Farm;
import com.example.swinecore.entity.FeedShipment;
import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.enums.PaymentStatus;
import com.example.swinecore.repository.FeedShipmentRepository;
import com.example.swinecore.repository.FinanceTransactionRepository;
import com.example.swinecore.entity.FinanceTransaction;
import com.example.swinecore.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedShipmentService {

    private final FeedShipmentRepository shipmentRepo;
    private final FinanceTransactionRepository financeRepo;
    private final InventoryService inventoryService;
    private final BuildingService buildingService;

    public FeedShipment save(FeedShipment shipment) {
        return shipmentRepo.save(shipment);
    }

    public List<FeedShipment> findByFarm(Farm farm) {
        return shipmentRepo.findByFarm(farm);
    }

    public Optional<FeedShipment> findById(Long id) {
        return shipmentRepo.findById(id);
    }

    /** Supervisor logs received shipment; auto-verify or alert on mismatch */
    public FeedShipment logReceived(FeedShipment shipment) {
        shipment = shipmentRepo.save(shipment);
        // Auto-verify if quantities match
        if (shipment.getDispatchQuantityKg() != null &&
            Math.abs(shipment.getDispatchQuantityKg() - shipment.getQuantityKg()) < 0.01) {
            shipment.setVerified(true);
            shipment.setPaymentReleased(true);
            shipment.setOverrideStatus(FeedShipment.OverrideStatus.AUTO_APPROVED);
            releasePayment(shipment);
        } else {
            shipment.setOverrideStatus(FeedShipment.OverrideStatus.PENDING_MANAGER_OVERRIDE);
        }
        return shipmentRepo.save(shipment);
    }

    /** Compare the supervisor's physical receipt against the factory dispatch report. */
    public FeedShipment supervisorVerify(Long id, Building supervisorBuilding,
                                         String receivedFeedType, double receivedQuantityKg) {
        FeedShipment shipment = shipmentRepo.findById(id).orElseThrow();
        if (!shipment.getFarm().getId().equals(supervisorBuilding.getFarm().getId()))
            throw new SecurityException("Shipment is outside your farm.");
        Long targetBuildingId = inventoryService.buildingId(shipment.getFeedType());
        if (targetBuildingId == null || !targetBuildingId.equals(supervisorBuilding.getId()))
            throw new SecurityException("Shipment is outside your building.");
        if (shipment.getOverrideStatus() != FeedShipment.OverrideStatus.PENDING_VERIFICATION)
            throw new IllegalStateException("This factory report has already been reviewed.");
        shipment.setQuantityKg(receivedQuantityKg);
        shipment.setManagerOverrideReason("SUPERVISOR_FEED=" + receivedFeedType.trim());
        boolean feedMatches = inventoryService.displayFeedType(shipment.getFeedType()).equalsIgnoreCase(receivedFeedType.trim());
        boolean quantityMatches = Math.abs(shipment.getDispatchQuantityKg() - receivedQuantityKg) < 0.01;
        if (feedMatches && quantityMatches) {
            shipment.setVerified(true);
            shipment.setPaymentReleased(true);
            shipment.setOverrideStatus(FeedShipment.OverrideStatus.AUTO_APPROVED);
            releasePayment(shipment);
        } else {
            shipment.setVerified(false);
            shipment.setOverrideStatus(FeedShipment.OverrideStatus.PENDING_MANAGER_OVERRIDE);
        }
        return shipmentRepo.save(shipment);
    }

    /** Manager confirms mismatch and authorizes double-entry log */
    public void managerConfirm(Long id, String reason) {
        FeedShipment shipment = shipmentRepo.findById(id).orElseThrow();
        shipment.setManagerConfirmed(true);
        shipment.setVerified(true);
        shipment.setPaymentReleased(true);
        shipment.setManagerOverrideReason(reason);
        shipment.setOverrideStatus(FeedShipment.OverrideStatus.CONFIRMED);
        releasePayment(shipment);
        shipmentRepo.save(shipment);
    }

    /** Manager rejects the discrepancy — purges dispute buffer and fires alert */
    public void managerReject(Long id, String reason) {
        FeedShipment shipment = shipmentRepo.findById(id).orElseThrow();
        if (reason == null || reason.isBlank())
            throw new IllegalArgumentException("A rejection description is required.");
        shipment.setManagerRejected(true);
        shipment.setManagerOverrideReason(reason.trim());
        shipment.setOverrideStatus(FeedShipment.OverrideStatus.REJECTED);
        shipmentRepo.save(shipment);
    }

    private void releasePayment(FeedShipment shipment) {
        if (financeRepo.findByReferenceId(shipment.getInvoiceNumber()).isPresent()) return;
        double total = shipment.getDispatchQuantityKg() * shipment.getPricePerKg();
        shipment.setTotalAmount(total);
        financeRepo.save(FinanceTransaction.builder()
            .farm(shipment.getFarm())
            .type("EXPENSE")
            .category("FEED_PURCHASE")
            .amount(shipment.getTotalAmount())
            .description("Feed shipment from " + shipment.getSupplierName()
                + " — Invoice: " + shipment.getInvoiceNumber())
            .referenceId(shipment.getInvoiceNumber())
            .status(PaymentStatus.COMPLETED)
            .build());
        // Update inventory
        double received = shipment.getQuantityKg() == null ? shipment.getDispatchQuantityKg() : shipment.getQuantityKg();
        Long buildingId = inventoryService.buildingId(shipment.getFeedType());
        Building building = buildingId == null ? null : buildingService.findById(buildingId).orElse(null);
        if (building == null) throw new IllegalStateException("A valid building is required before inventory can be updated.");
        inventoryService.addFactoryStock(building, inventoryService.displayFeedType(shipment.getFeedType()), received);
    }
}
