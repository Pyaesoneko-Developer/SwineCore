package com.example.swinecore.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tracks which Staff member has the birth-recording form open for a given building.
 * Only ONE record per building at a time.
 */
@Entity
@Table(name = "birth_form_rotation",
       uniqueConstraints = @UniqueConstraint(columnNames = "building_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BirthFormRotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    /** Staff member currently holding the form */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "current_staff_id", nullable = false)
    private User currentStaff;

    /** Date the form was assigned / last opened */
    private LocalDate assignedDate;

    /** Whether staff submitted a record today */
    @Builder.Default
    private boolean submittedToday = false;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
