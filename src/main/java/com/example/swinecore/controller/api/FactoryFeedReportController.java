package com.example.swinecore.controller.api;

import com.example.swinecore.entity.*;
import com.example.swinecore.repository.*;
import com.example.swinecore.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/factory")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FactoryFeedReportController {
    private final FarmService farmService;
    private final BuildingService buildingService;
    private final FeedShipmentService shipmentService;
    private final FeedShipmentRepository shipmentRepo;
    private final InventoryRepository inventoryRepo;
    private final InventoryService inventoryService;

    @GetMapping("/catalog")
    public List<Map<String,Object>> catalog() {
        return farmService.findAll().stream().map(farm -> {
            Map<String,Object> item = new LinkedHashMap<>();
            item.put("id", farm.getId()); item.put("name", farm.getName());
            item.put("buildings", buildingService.findByFarm(farm).stream()
                .map(b -> Map.of("id", b.getId(), "name", b.getName(), "code", b.getCode())).toList());
            return item;
        }).toList();
    }

    @GetMapping("/stock-shortages")
    public List<Map<String,Object>> stockShortages() {
        return inventoryService.getAlertViews().stream().map(inv -> {
            Map<String,Object> alert = new LinkedHashMap<>();
            alert.put("farmId", inv.farm().getId()); alert.put("farm", inv.farm().getName());
            alert.put("buildingId", inv.building() == null ? null : inv.building().getId());
            alert.put("building", inv.building() == null ? "Legacy farm stock" : inv.building().getName());
            alert.put("feedType", inv.feedType()); alert.put("quantityKg", inv.quantityKg());
            alert.put("dailyConsumptionKg", inv.dailyConsumptionKg()); alert.put("remainingDays", inv.remainingDays());
            alert.put("thresholdKg", inv.alertThresholdKg());
            alert.put("warning", "STOCK_SHORTAGE_WARNING");
            return alert;
        }).toList();
    }

    @PostMapping("/reports")
    public ResponseEntity<Map<String,Object>> submit(@RequestBody FactoryReportRequest request) {
        var existing = shipmentRepo.findByInvoiceNumber(request.invoiceNumber());
        if (existing.isPresent() && existing.get().getOverrideStatus() == FeedShipment.OverrideStatus.REJECTED)
            shipmentRepo.delete(existing.get());
        else if (existing.isPresent()) throw new IllegalArgumentException("Invoice number already submitted.");
        Farm farm = farmService.findById(request.farmId()).orElseThrow();
        Building building = buildingService.findById(request.buildingId()).orElseThrow();
        if (!building.getFarm().getId().equals(farm.getId()))
            throw new IllegalArgumentException("Building does not belong to the selected farm.");
        if (request.quantityKg() <= 0 || request.pricePerKg() <= 0)
            throw new IllegalArgumentException("Quantity and price must be greater than zero.");
        FeedShipment report = FeedShipment.builder()
            .farm(farm).feedType(inventoryService.stockKey(building.getId(), request.feedType()))
            .dispatchQuantityKg(request.quantityKg()).pricePerKg(request.pricePerKg())
            .totalAmount(request.quantityKg() * request.pricePerKg())
            .supplierName(request.supplierName().trim()).invoiceNumber(request.invoiceNumber().trim())
            .shipmentDate(request.shipmentDate() == null ? LocalDate.now() : request.shipmentDate())
            .overrideStatus(FeedShipment.OverrideStatus.PENDING_VERIFICATION).build();
        report = shipmentService.save(report);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "reportId", report.getId(), "status", report.getOverrideStatus(),
            "totalAmount", report.getTotalAmount(), "message", "Factory report submitted."
        ));
    }

    @GetMapping("/reports/status")
    public Map<String,Object> status(@RequestParam String invoiceNumber) {
        FeedShipment s = shipmentRepo.findByInvoiceNumber(invoiceNumber).orElseThrow();
        return Map.of("invoiceNumber", invoiceNumber, "status", s.getOverrideStatus(),
            "paymentReleased", s.isPaymentReleased(), "totalAmount", s.getTotalAmount());
    }

    @GetMapping("/rejections")
    public List<Map<String,Object>> rejections(@RequestParam String supplierName) {
        return shipmentRepo.findAll().stream()
            .filter(s -> s.getOverrideStatus() == FeedShipment.OverrideStatus.REJECTED)
            .filter(s -> s.getSupplierName().equalsIgnoreCase(supplierName))
            .map(s -> Map.<String,Object>of("invoiceNumber", s.getInvoiceNumber(),
                "farmName", s.getFarm().getName(), "buildingName", buildingName(s),
                "reason", s.getManagerOverrideReason())).toList();
    }

    private String buildingName(FeedShipment shipment) {
        Long id = inventoryService.buildingId(shipment.getFeedType());
        return id == null ? "Unknown" : buildingService.findById(id).map(Building::getName).orElse("Unknown");
    }

    public record FactoryReportRequest(Long farmId, Long buildingId, String feedType,
        double quantityKg, double pricePerKg, String supplierName, String invoiceNumber,
        LocalDate shipmentDate) {}

    @ExceptionHandler({IllegalArgumentException.class, NoSuchElementException.class})
    public ResponseEntity<Map<String,String>> error(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
