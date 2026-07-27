package com.example.swinecore.entity;

import com.example.swinecore.entity.enums.PigGender;
import com.example.swinecore.entity.enums.PigStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pigs", indexes = {
        @Index(name = "idx_pig_code", columnList = "code"),
        @Index(name = "idx_pig_building", columnList = "building_id"),
        @Index(name = "idx_pig_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PigGender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PigStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "genetics_id", nullable = false)
    private Genetics genetics;

    @ManyToOne
    @JoinColumn(name = "mother_id")
    private Pig mother;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "birth_record_id")
    private BirthRecord birthRecord;

    @OneToMany(mappedBy = "mother", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Pig> offspring = new ArrayList<>();

    private LocalDate birthDate;
    private LocalDate recordedDate;
    private LocalDate soldDate;
    private LocalDate listedForSaleDate;

    private Double currentWeight;
    private String photoPath;
    private String notes;

    @Builder.Default
    private boolean listedForSale = false;

    private Double salePrice;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public long getAgeInDays() {
        if (birthDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(birthDate, LocalDate.now());
    }
}