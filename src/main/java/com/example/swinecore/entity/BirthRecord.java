package com.example.swinecore.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "birth_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BirthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mother_id", nullable = false)
    private Pig mother;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by")
    private User recordedBy;

    private LocalDate birthDate;
    private int litterSize;
    private int alivePiglets;
    private int deadPiglets;

    /** Generated pig codes for this litter */
    // @OneToMany(mappedBy = "mother", cascade = CascadeType.ALL, fetch =
    // FetchType.LAZY)
    // private List<Pig> piglets = new ArrayList<>();

    // @OneToMany(mappedBy = "mother")
    // private List<Pig> piglets;

    @OneToMany(mappedBy = "birthRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Pig> piglets = new ArrayList<>();

    @Builder.Default
    private boolean confirmedByStaff = false;
    @Builder.Default
    private boolean confirmedBySupervisor = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
