package com.example.swinecore.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "farm_ads")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FarmAd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    private String title;
    private String description;
    private String imagePath;
    @Builder.Default
    private boolean active = true;

    /** Blueprint: Category dropdown (Live Breeding Livestock, High-Quality Semen Supply, Farm Promotions) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AdCategory category = AdCategory.LIVE_LIVESTOCK;

    private String contactInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum AdCategory {
        LIVE_LIVESTOCK,
        SEMEN_SUPPLY,
        FARM_PROMOTIONS
    }
}
