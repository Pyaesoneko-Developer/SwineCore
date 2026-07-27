package com.example.swinecore.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "feed_shipments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeedShipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by")
    private User receivedBy;

    private String feedType;
    private Double quantityKg;
    private Double pricePerKg;
    private Double totalAmount;
    private LocalDate shipmentDate;
    private String supplierName;
    private String invoiceNumber;

    /** Dispatch report quantity from supplier */
    private Double dispatchQuantityKg;

    /** True when dispatch and received quantities match */
    @Builder.Default
    private boolean verified = false;

    /** Manager manually confirmed mismatch */
    @Builder.Default
    private boolean managerConfirmed = false;

    /** Manager rejected the discrepancy — forces resubmission */
    @Builder.Default
    private boolean managerRejected = false;

    private String managerOverrideReason;

    /** Payment released */
    @Builder.Default
    private boolean paymentReleased = false;

    /** Blueprint status: AUTO_APPROVED, PENDING_MANAGER_OVERRIDE, CONFIRMED, REJECTED */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OverrideStatus overrideStatus = OverrideStatus.PENDING_VERIFICATION;

    public enum OverrideStatus {
        PENDING_VERIFICATION,
        AUTO_APPROVED,
        PENDING_MANAGER_OVERRIDE,
        CONFIRMED,
        REJECTED
    }

    @Transient
    public Long getTargetBuildingId() {
        try { return Long.valueOf(feedType.substring(0, feedType.indexOf("::"))); }
        catch (Exception e) { return null; }
    }

    @Transient
    public String getDisplayFeedType() {
        int index = feedType == null ? -1 : feedType.indexOf("::");
        return index < 0 ? feedType : feedType.substring(index + 2);
    }

    @Transient
    public String getReceivedFeedType() {
        String prefix = "SUPERVISOR_FEED=";
        return managerOverrideReason != null && managerOverrideReason.startsWith(prefix)
            ? managerOverrideReason.substring(prefix.length()) : null;
    }

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
