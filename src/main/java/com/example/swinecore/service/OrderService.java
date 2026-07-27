package com.example.swinecore.service;

import com.example.swinecore.entity.*;
import com.example.swinecore.entity.enums.OrderStatus;
import com.example.swinecore.entity.enums.PaymentStatus;
import com.example.swinecore.repository.*;
import com.example.swinecore.util.QrCodeUtil;
import com.example.swinecore.util.NetworkBaseUrlResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final PigOrderRepository pigOrderRepository;
    private final PigOrderItemRepository pigOrderItemRepository;
    private final SemenOrderRepository semenOrderRepository;
    private final PigRepository pigRepository;
    private final FinanceTransactionRepository financeRepo;
    private final QrCodeUtil qrCodeUtil;
    private final NetworkBaseUrlResolver networkBaseUrlResolver;
    private final RuleScheduleService ruleScheduleService;

    // ---- Pig Orders ----

    public PigOrder createPigOrder(CustomerAccount customer, List<Long> pigIds) {
        if (pigIds == null || pigIds.isEmpty())
            throw new IllegalArgumentException("Select at least one pig before checkout.");
        List<Long> uniquePigIds = pigIds.stream().distinct().toList();
        PigOrder order = PigOrder.builder()
            .customer(customer)
            .status(OrderStatus.PENDING)
            .orderReference(generateRef())
            .build();

        double total = 0;
        for (Long pigId : uniquePigIds) {
            Pig pig = pigRepository.findById(pigId)
                .orElseThrow(() -> new IllegalArgumentException("Pig not found: " + pigId));
            if (!pig.isListedForSale())
                throw new IllegalArgumentException("Pig not available for sale: " + pig.getCode());
            if (pig.getSalePrice() == null || pig.getSalePrice() <= 0)
                throw new IllegalArgumentException("Pig has no valid sale price: " + pig.getCode());
            PigOrderItem item = PigOrderItem.builder().order(order).pig(pig)
                .unitPrice(pig.getSalePrice()).build();
            order.getItems().add(item);
            total += pig.getSalePrice() != null ? pig.getSalePrice() : 0;
        }
        order.setTotalAmount(total);
        order = pigOrderRepository.save(order);

        // QR images are generated on demand. Storing base64 in the legacy VARCHAR
        // column caused checkout failures and is unnecessary.
        order.setQrCodePath(null);
        order.setStatus(OrderStatus.QR_GENERATED);
        return pigOrderRepository.save(order);
    }

    public PigOrder completePigPayment(String reference) {
        PigOrder order = pigOrderRepository.findLockedByOrderReference(reference)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.DISPATCHED) {
            return order;
        }
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());

        // Mark pigs as sold
        for (PigOrderItem item : order.getItems()) {
            Pig pig = item.getPig();
            pig.setStatus(com.example.swinecore.entity.enums.PigStatus.SOLD);
            pig.setSoldDate(java.time.LocalDate.now());
            pig.setListedForSale(false);
            pigRepository.save(pig);
        }

        // Log income
        if (!financeRepo.existsByReferenceId(reference)) {
            Farm farm = order.getItems().stream().findFirst()
                .map(item -> item.getPig().getBuilding().getFarm()).orElse(null);
            financeRepo.save(FinanceTransaction.builder()
                .farm(farm).type("INCOME").category("PIG_SALE")
                .amount(order.getTotalAmount())
                .description("Pig sale order " + reference)
                .referenceId(reference)
                .status(PaymentStatus.COMPLETED)
                .build());
        }

        return pigOrderRepository.save(order);
    }

    // ---- Semen Orders ----

    public SemenOrder createSemenOrder(CustomerAccount customer, Long boarId, int qty) {
        if (qty < 1 || qty > 100)
            throw new IllegalArgumentException("Quantity must be between 1 and 100 doses.");
        Pig boar = pigRepository.findById(boarId)
            .orElseThrow(() -> new IllegalArgumentException("Boar not found"));
        if (boar.getStatus() != com.example.swinecore.entity.enums.PigStatus.BREEDING_BOAR
                || boar.getGender() != com.example.swinecore.entity.enums.PigGender.MALE)
            throw new IllegalArgumentException("The selected pig is not an available breeding boar.");
        double pricePerUnit = ruleScheduleService.getSemenPrice(boar.getBuilding().getFarm());
        double total = qty * pricePerUnit;

        SemenOrder order = SemenOrder.builder()
            .customer(customer).boar(boar).quantity(qty)
            .pricePerUnit(pricePerUnit).totalAmount(total)
            .status(OrderStatus.PENDING)
            .orderReference(generateRef())
            .build();
        order = semenOrderRepository.save(order);

        order.setQrCodePath(null);
        order.setStatus(OrderStatus.QR_GENERATED);
        return semenOrderRepository.save(order);
    }

    public SemenOrder completeSemenPayment(String reference) {
        SemenOrder order = semenOrderRepository.findLockedByOrderReference(reference)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.DISPATCHED) {
            return order;
        }
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());

        if (!financeRepo.existsByReferenceId(reference)) {
            financeRepo.save(FinanceTransaction.builder()
                .farm(order.getBoar().getBuilding().getFarm())
                .type("INCOME").category("SEMEN_SALE")
                .amount(order.getTotalAmount())
                .description("Semen order " + reference)
                .referenceId(reference)
                .status(PaymentStatus.COMPLETED)
                .build());
        }

        return semenOrderRepository.save(order);
    }

    public Optional<PigOrder> findPigOrderByRef(String ref) {
        return pigOrderRepository.findByOrderReference(ref);
    }

    public String generatePigQrImage(PigOrder order) {
        String farm = order.getItems().stream().map(i -> i.getPig().getBuilding().getFarm().getName())
            .distinct().reduce((a, b) -> a + ", " + b).orElse("Unknown Farm");
        String details = order.getItems().stream().map(i -> i.getPig().getCode() + " (" +
            i.getPig().getBuilding().getFarm().getName() + ")").reduce((a, b) -> a + ", " + b).orElse("Pig order");
        return generateQr(order.getOrderReference(), "PIG", order.getTotalAmount(), farm, details);
    }

    public String generateSemenQrImage(SemenOrder order) {
        String farm = order.getBoar().getBuilding().getFarm().getName();
        String details = order.getQuantity() + " dose(s) from boar " + order.getBoar().getCode();
        return generateQr(order.getOrderReference(), "SEMEN", order.getTotalAmount(), farm, details);
    }

    private String generateQr(String ref, String type, double total, String farm, String details) {
        try {
            String content = qrCodeUtil.buildCustomerPaymentUrl(
                networkBaseUrlResolver.resolve(), ref, type, total, farm, details);
            return qrCodeUtil.generateBase64QrCode(content, 300, 300);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to generate payment QR code.", e);
        }
    }

    public Optional<SemenOrder> findSemenOrderByRef(String ref) {
        return semenOrderRepository.findByOrderReference(ref);
    }

    public PigOrder getCustomerPigOrder(Long id, CustomerAccount customer) {
        PigOrder order = pigOrderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pig order not found"));
        if (!order.getCustomer().getId().equals(customer.getId()))
            throw new IllegalArgumentException("You cannot access this order");
        return order;
    }

    public SemenOrder getCustomerSemenOrder(Long id, CustomerAccount customer) {
        SemenOrder order = semenOrderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Semen order not found"));
        if (!order.getCustomer().getId().equals(customer.getId()))
            throw new IllegalArgumentException("You cannot access this order");
        return order;
    }

    public Object getCustomerOrderByReference(String reference, CustomerAccount customer) {
        Optional<PigOrder> pig = pigOrderRepository.findByOrderReference(reference);
        if (pig.isPresent()) {
            if (!pig.get().getCustomer().getId().equals(customer.getId()))
                throw new IllegalArgumentException("You cannot access this order");
            return pig.get();
        }
        SemenOrder semen = semenOrderRepository.findByOrderReference(reference)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!semen.getCustomer().getId().equals(customer.getId()))
            throw new IllegalArgumentException("You cannot access this order");
        return semen;
    }

    @Transactional(readOnly = true)
    public PaymentVoucher getPaymentVoucher(String reference, String type, String amount,
                                             String farm, String details) {
        double signedAmount;
        try {
            signedAmount = Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid payment amount");
        }

        Optional<PigOrder> pig = pigOrderRepository.findByOrderReference(reference);
        if (pig.isPresent()) {
            PigOrder order = pig.get();
            String actualFarm = pigFarm(order);
            String actualDetails = pigDetails(order);
            validateVoucher("PIG", order.getTotalAmount(), actualFarm, actualDetails,
                type, signedAmount, farm, details);
            return new PaymentVoucher(reference, "PIG", actualFarm, actualDetails,
                order.getTotalAmount(), order.getStatus(), order.getCustomer().getName(),
                order.getCustomer().getPhone(), order.getPaidAt());
        }

        SemenOrder order = semenOrderRepository.findByOrderReference(reference)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        String actualFarm = order.getBoar().getBuilding().getFarm().getName();
        String actualDetails = order.getQuantity() + " dose(s) from boar " + order.getBoar().getCode();
        validateVoucher("SEMEN", order.getTotalAmount(), actualFarm, actualDetails,
            type, signedAmount, farm, details);
        return new PaymentVoucher(reference, "SEMEN", actualFarm, actualDetails,
            order.getTotalAmount(), order.getStatus(), order.getCustomer().getName(),
            order.getCustomer().getPhone(), order.getPaidAt());
    }

    public PaymentVoucher confirmSignedPayment(String reference, String type, String amount,
                                                String farm, String details) {
        PaymentVoucher voucher = getPaymentVoucher(reference, type, amount, farm, details);
        if (voucher.status() != OrderStatus.PAID && voucher.status() != OrderStatus.DISPATCHED) {
            if ("PIG".equals(voucher.type())) completePigPayment(reference);
            else completeSemenPayment(reference);
        }
        return getPaymentVoucher(reference, type, amount, farm, details);
    }

    private String pigFarm(PigOrder order) {
        return order.getItems().stream().map(i -> i.getPig().getBuilding().getFarm().getName())
            .distinct().reduce((a, b) -> a + ", " + b).orElse("Unknown Farm");
    }

    private String pigDetails(PigOrder order) {
        return order.getItems().stream().map(i -> i.getPig().getCode() + " (" +
            i.getPig().getBuilding().getFarm().getName() + ")")
            .reduce((a, b) -> a + ", " + b).orElse("Pig order");
    }

    private void validateVoucher(String actualType, double actualAmount, String actualFarm,
                                 String actualDetails, String type, double amount,
                                 String farm, String details) {
        if (!actualType.equals(type) || Math.abs(actualAmount - amount) > 0.009
                || !actualFarm.equals(farm) || !actualDetails.equals(details)) {
            throw new IllegalArgumentException("Voucher data does not match the order");
        }
    }

    public record PaymentVoucher(String reference, String type, String farm, String details,
        Double total, OrderStatus status, String customerName, String phone, LocalDateTime paidAt) {}

    public List<PigOrder> getCustomerPigOrders(CustomerAccount customer) {
        return pigOrderRepository.findByCustomer(customer);
    }

    public List<SemenOrder> getCustomerSemenOrders(CustomerAccount customer) {
        return semenOrderRepository.findByCustomer(customer);
    }

    /** Account history only contains orders whose payment has completed. */
    public List<PigOrder> getCustomerPaidPigOrders(CustomerAccount customer) {
        return pigOrderRepository.findByCustomer(customer).stream()
            .filter(order -> order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.DISPATCHED)
            .toList();
    }

    /** Account history only contains orders whose payment has completed. */
    public List<SemenOrder> getCustomerPaidSemenOrders(CustomerAccount customer) {
        return semenOrderRepository.findByCustomer(customer).stream()
            .filter(order -> order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.DISPATCHED)
            .toList();
    }

    private String generateRef() {
        return "SC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
