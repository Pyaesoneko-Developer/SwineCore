package com.example.swinecore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "shifts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Shift {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @Column(nullable = false)
    private LocalTime standardStart;

    @Column(nullable = false)
    private LocalTime standardEnd;

    @Column(nullable = false)
    @Builder.Default
    private Integer gracePeriodMinutes = 15;

    @Column(nullable = false)
    @Builder.Default
    private Double minimumRequiredHours = 8.0;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
