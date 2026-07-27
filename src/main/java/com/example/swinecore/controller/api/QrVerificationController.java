package com.example.swinecore.controller.api;

import com.example.swinecore.entity.PigOrder;
import com.example.swinecore.entity.SemenOrder;
import com.example.swinecore.entity.enums.OrderStatus;
import com.example.swinecore.entity.enums.PigStatus;
import com.example.swinecore.repository.PigOrderRepository;
import com.example.swinecore.repository.PigRepository;
import com.example.swinecore.repository.SemenOrderRepository;
import com.example.swinecore.util.QrCodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QrVerificationController {

    private final QrCodeUtil qrCodeUtil;
    private final PigOrderRepository pigOrderRepository;
    private final SemenOrderRepository semenOrderRepository;
    private final PigRepository pigRepository;

    @PostMapping("/verify")
    @PreAuthorize("hasAnyRole('SUPERVISOR','MANAGER','ADMIN')")
    public ResponseEntity<Map<String, Object>> verifyQr(@RequestBody Map<String, String> request) {
        String orderRef = request.get("orderReference");
        String qrPayload = request.get("qrPayload");
        String signature = request.get("signature");

        if (orderRef == null || orderRef.isBlank()
                || qrPayload == null || qrPayload.isBlank()
                || signature == null || signature.isBlank()) {

            return ResponseEntity.badRequest().body(Map.of(
                    "verified", false,
                    "message", "Missing required fields: orderReference, qrPayload, signature"
            ));
        }

        if (!qrCodeUtil.verifySignature(qrPayload, signature)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "verified", false,
                    "message", "Signature verification failed. QR code integrity compromised."
            ));
        }

        var pigOrder = pigOrderRepository.findByOrderReference(orderRef);

        if (pigOrder.isPresent()) {
            return verifyPigOrder(pigOrder.get(), orderRef);
        }

        var semenOrder = semenOrderRepository.findByOrderReference(orderRef);

        if (semenOrder.isPresent()) {
            return verifySemenOrder(semenOrder.get(), orderRef);
        }

        return ResponseEntity.status(404).body(Map.of(
                "verified", false,
                "message", "Order not found: " + orderRef
        ));
    }

    private ResponseEntity<Map<String, Object>> verifyPigOrder(PigOrder order, String orderRef) {
        if (order.getStatus() == OrderStatus.PAID
                || order.getStatus() == OrderStatus.QR_GENERATED) {

            order.setStatus(OrderStatus.DISPATCHED);
            order.setPaidAt(LocalDateTime.now());
            pigOrderRepository.save(order);

            if (order.getItems() != null) {
                for (var item : order.getItems()) {
                    if (item.getPig() == null) {
                        continue;
                    }

                    var pig = item.getPig();

                    /*
                     * Current PigStatus:
                     * PIGLET, GROWER, FINISHER, BREEDING_SOW, BREEDING_BOAR,
                     * PENDING_SALE_APPROVAL, FOR_SALE, SOLD
                     *
                     * DISPATCHED_AND_SOLD does not exist, so use SOLD.
                     */
                    pig.setStatus(PigStatus.SOLD);
                    pig.setSoldDate(LocalDate.now());
                    pig.setListedForSale(false);
                    pig.setListedForSaleDate(null);

                    pigRepository.save(pig);
                }
            }

            return ResponseEntity.ok(Map.of(
                    "verified", true,
                    "message", "Pig order dispatched and pigs marked as SOLD successfully.",
                    "orderReference", orderRef,
                    "type", "PIG",
                    "amount", order.getTotalAmount()
            ));
        }

        return ResponseEntity.ok(Map.of(
                "verified", true,
                "message", "Order already in status: " + order.getStatus(),
                "orderReference", orderRef,
                "type", "PIG"
        ));
    }

    private ResponseEntity<Map<String, Object>> verifySemenOrder(SemenOrder order, String orderRef) {
        if (order.getStatus() == OrderStatus.PAID
                || order.getStatus() == OrderStatus.QR_GENERATED) {

            order.setStatus(OrderStatus.DISPATCHED);
            order.setPaidAt(LocalDateTime.now());
            semenOrderRepository.save(order);

            return ResponseEntity.ok(Map.of(
                    "verified", true,
                    "message", "Semen order dispatched successfully.",
                    "orderReference", orderRef,
                    "type", "SEMEN",
                    "amount", order.getTotalAmount()
            ));
        }

        return ResponseEntity.ok(Map.of(
                "verified", true,
                "message", "Order already in status: " + order.getStatus(),
                "orderReference", orderRef,
                "type", "SEMEN"
        ));
    }
}