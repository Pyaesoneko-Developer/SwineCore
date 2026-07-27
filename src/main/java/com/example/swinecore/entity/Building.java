package com.example.swinecore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buildings", indexes = {
    @Index(name = "idx_building_farm", columnList = "farm_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    /** Short code used in pig serial generation (e.g., "B1") */
    @Column(nullable = false, length = 4)
    private String code;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @OneToMany(mappedBy = "building", fetch = FetchType.LAZY)
    @Builder.Default
    private List<User> staff = new ArrayList<>();

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Pig> pigs = new ArrayList<>();

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Room> rooms = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** Birth-record form rotation index (tracks which staff member's turn it is) */
    @Builder.Default
    private int birthFormRotationIndex = 0;

    @Transient
    public User getCurrentSupervisor() {
        return staff.stream()
            .filter(u -> u.getRole().name().equals("SUPERVISOR") && u.isEnabled())
            .findFirst().orElse(null);
    }
}
