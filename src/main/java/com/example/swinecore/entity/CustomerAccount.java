package com.example.swinecore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_accounts", indexes = {
    @Index(name = "idx_cust_email", columnList = "email")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerAccount {

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

    private String phone;
    private String address;
    private String profileImagePath;
    @Builder.Default
    private boolean enabled = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** Alias used throughout templates via ${customer.fullName}. */
    public String getFullName() {
        return name != null ? name : "";
    }

    /**
     * Returns 1–2 uppercase initials derived from the name.
     * "Aye Chan" → "AC",  "Aye" → "A"
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
