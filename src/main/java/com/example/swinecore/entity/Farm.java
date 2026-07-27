package com.example.swinecore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "farms")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    /** Short code used in pig serial generation (e.g., "AA") */
    @Column(nullable = false, unique = true, length = 4)
    private String code;

    private String location;
    private String description;
    private String imagePath;

    /** Geographic coordinates for Myanmar interactive map */
    private Double latitude;
    private Double longitude;

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Building> buildings = new ArrayList<>();

    @OneToMany(mappedBy = "farm", fetch = FetchType.LAZY)
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RuleSchedule> ruleSchedules = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** Convenience: current manager (single at a time) */
    @Transient
    public User getCurrentManager() {
        return users.stream()
            .filter(u -> u.getRole().name().equals("MANAGER") && u.isEnabled())
            .findFirst().orElse(null);
    }
}
