package com.example.swinecore.entity;

import com.example.swinecore.entity.enums.RuleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rule_schedules", indexes = {
    @Index(name = "idx_rule_farm", columnList = "farm_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RuleSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleType ruleType;

    /** Day from birth when rule applies (e.g., 30 = day 30 from birth) */
    private Integer dayFromBirth;

    /** For range-based rules: start day */
    private Integer dayRangeStart;

    /** For range-based rules: end day */
    private Integer dayRangeEnd;

    /** Feed amount in kg (for feeding rules) */
    private Double feedAmountKg;

    /** Feed type/name */
    private String feedType;

    /** Medication or vaccine name */
    private String medication;

    /** Dosage description (e.g., "5ml", "10ml") */
    private String dosage;

    /** Administration route for vaccines: INTRAMUSCULAR, SUBCUTANEOUS */
    private String administrationRoute;

    /** Fixed target day for vaccines/medications (single integer anchor) */
    private Integer fixedTargetDay;

    /** For breeding sow rules: month number (1-3) or day 115 */
    private Integer breedingMonth;

    /** Applies to: PIGLET, GROWER, FINISHER, BREEDING_SOW, BREEDING_BOAR, ALL */
    @Builder.Default
    private String appliesTo = "ALL";

    private String description;
    @Builder.Default
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
