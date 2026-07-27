package com.example.swinecore.entity;

import com.example.swinecore.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_role", columnList = "role")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Email @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** Farm this user primarily belongs to (null for Admin & HR who span all farms) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id")
    private Farm farm;

    /** Building this Supervisor/Staff is assigned to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private Building building;

    /** Date the user took over the current role assignment */
    private LocalDate startDate;

    private String phone;
    private String profileImagePath;

    @Builder.Default
    private boolean enabled = true;
    @Builder.Default
    private boolean mustChangePassword = false;

    private String passwordResetToken;
    private LocalDateTime passwordResetTokenExpiry;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /** Alias used throughout templates via ${user.fullName}. */
    public String getFullName() {
        return name != null ? name : "";
    }

    /**
     * Returns 1–2 uppercase initials derived from the name.
     */
    public String getInitials() {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        String first = parts[0].substring(0, 1).toUpperCase();
        if (parts.length == 1) return first;
        String last = parts[parts.length - 1].substring(0, 1).toUpperCase();
        return first + last;
    }
}
