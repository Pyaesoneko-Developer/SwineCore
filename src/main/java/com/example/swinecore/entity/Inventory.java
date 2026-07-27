package com.example.swinecore.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory", indexes = {
    @Index(name = "idx_inv_farm_type", columnList = "farm_id, feed_type")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @Column(nullable = false)
    private String feedType;

    @Builder.Default
    private Double quantityKg = 0.0;

    /** Low-stock alert threshold in kg (auto-computed as 2-day supply) */
    @Builder.Default
    private Double alertThresholdKg = 0.0;


    @Builder.Default
    private boolean alertTriggered = false;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
