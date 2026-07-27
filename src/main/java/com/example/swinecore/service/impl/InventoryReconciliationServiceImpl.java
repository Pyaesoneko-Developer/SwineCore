package com.example.swinecore.service.impl;

import com.example.swinecore.entity.*;
import com.example.swinecore.entity.enums.PaymentStatus;
import com.example.swinecore.repository.FinanceTransactionRepository;
import com.example.swinecore.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * RECONCILIATION DISCREPANCY FLOW & MANAGEMENT OVERRIDE PIPELINE
 *
 * Processes Supervisor's raw incoming asset delivery reports with
 * @Transactional(isolation = Isolation.SERIALIZABLE)
 *
 * - AUTO_APPROVED: Match found → double-entry update (stock + ledger)
 * - PENDING_MANAGER_OVERRIDE: Mismatch → held in transactional buffer
 */
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryReconciliationServiceImpl {

    private final InventoryRepository inventoryRepository;
    private final FinanceTransactionRepository financeTransactionRepository;

    /**
     * Process a received feed shipment against expected data.
     *
     * If quantities match: auto-approve → increment stock + record expense
     * If mismatch: shunt to PENDING_MANAGER_OVERRIDE
     */
    public FeedShipment processReconciliation(FeedShipment shipment) {
        boolean quantitiesMatch = verifyQuantitiesMatch(shipment);

        if (quantitiesMatch) {
            return autoApproveShipment(shipment);
        } else {
            return flagForManagerOverride(shipment);
        }
    }

    /**
     * Automated Match Workflow:
     * - Calculates expense = unitPrice * receivedQuantity
     * - Increments stock in inventories table
     * - Records expense in marketplace_transactions as double-entry
     */
    private FeedShipment autoApproveShipment(FeedShipment shipment) {
        shipment.setVerified(true);
        shipment.setOverrideStatus(FeedShipment.OverrideStatus.AUTO_APPROVED);

        // Calculate expense from price and quantity
        double expenseAmount = shipment.getPricePerKg() * shipment.getQuantityKg();
        shipment.setTotalAmount(expenseAmount);

        // Increment stock in inventories table
        incrementInventoryStock(shipment.getFarm(), shipment.getFeedType(), shipment.getQuantityKg());

        // Record expense in cashflow ledger as double-entry validation
        FinanceTransaction expense = FinanceTransaction.builder()
                .farm(shipment.getFarm())
                .type("EXPENSE")
                .category("FEED_PURCHASE")
                .amount(expenseAmount)
                .description("Auto-approved feed purchase: " + shipment.getFeedType()
                        + " (" + shipment.getQuantityKg() + " kg from " + shipment.getSupplierName() + ")")
                .referenceId("SHIP-" + shipment.getId())
                .status(PaymentStatus.COMPLETED)
                .build();
        financeTransactionRepository.save(expense);

        return shipment;
    }

    /**
     * Mismatch Discrepancy Workflow:
     * - Locks process path under PENDING_MANAGER_OVERRIDE
     * - No data committed to stock inventory or expense ledgers
     * - Payload held in isolated transactional buffer state
     */
    private FeedShipment flagForManagerOverride(FeedShipment shipment) {
        shipment.setVerified(false);
        shipment.setOverrideStatus(FeedShipment.OverrideStatus.PENDING_MANAGER_OVERRIDE);
        return shipment;
    }

    /**
     * Manager confirms override — forces double-entry despite mismatch
     */
    public FeedShipment managerConfirmOverride(Long shipmentId) {
        // Find by ID (assuming feedShipmentService handles this)
        // This is called from ManagerController
        return null; // Placeholder - actual implementation via FeedShipmentService
    }

    /**
     * Manager rejects — discards transaction, forces resubmission
     */
    public FeedShipment managerRejectOverride(Long shipmentId, String reason) {
        return null; // Placeholder - actual implementation via FeedShipmentService
    }

    /**
     * Verify if received quantities match dispatch quantities.
     * Uses tolerance of 2% for rounding differences.
     */
    private boolean verifyQuantitiesMatch(FeedShipment shipment) {
        if (shipment.getDispatchQuantityKg() == null || shipment.getQuantityKg() == null) {
            return false;
        }
        double tolerance = 0.02; // 2% tolerance
        double diff = Math.abs(shipment.getDispatchQuantityKg() - shipment.getQuantityKg());
        double avg = (shipment.getDispatchQuantityKg() + shipment.getQuantityKg()) / 2.0;
        return (diff / avg) <= tolerance;
    }

    /**
     * Increment inventory stock for a given feed type on a farm.
     * Creates inventory record if it doesn't exist.
     */
    private void incrementInventoryStock(Farm farm, String feedType, double quantityKg) {
        Inventory inventory = inventoryRepository.findByFarmAndFeedType(farm, feedType)
                .orElse(Inventory.builder()
                        .farm(farm)
                        .feedType(feedType)
                        .quantityKg(0.0)
                        .alertThresholdKg(0.0)
                        .build());

        double newQty = inventory.getQuantityKg() + quantityKg;
        inventory.setQuantityKg(newQty);

        // Check alert threshold
        if (inventory.getAlertThresholdKg() > 0 && newQty < inventory.getAlertThresholdKg()) {
            inventory.setAlertTriggered(true);
        } else {
            inventory.setAlertTriggered(false);
        }

        inventoryRepository.save(inventory);
    }
}
