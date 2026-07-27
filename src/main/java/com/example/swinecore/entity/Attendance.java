package com.example.swinecore.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance", indexes = {
    @Index(name = "idx_att_user_date", columnList = "user_id, work_date")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate workDate;

    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;

    @Builder.Default
    private boolean attended = false;
    @Builder.Default
    private boolean clockedOut = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AttendanceStatus status = AttendanceStatus.NORMAL;

    private String earlyDepartureReason;

    private Double wageDeductionAmount;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum AttendanceStatus {
        NORMAL,
        EARLY_DEPARTURE,
        EXCUSED_EARLY_LEAVE,
        CONFIRMED_PENALTY
    }
}
