package com.example.swinecore.entity;

import com.example.swinecore.entity.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_tasks", indexes = {
    @Index(name = "idx_task_staff_date", columnList = "assigned_staff_id, task_date"),
    @Index(name = "idx_task_building_date", columnList = "building_id, task_date")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DailyTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_staff_id")
    private User assignedStaff;

    /** Rule that triggered this task (null for standard "care" tasks) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_schedule_id")
    private RuleSchedule ruleSchedule;

    /** Pig the task applies to (null for building-wide tasks) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pig_id")
    private Pig pig;

    @Column(nullable = false)
    private LocalDate taskDate;

    @Column(nullable = false)
    private String taskName;

    private String taskDescription;
    @Builder.Default
    private boolean isStandardTask = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskStatus status = TaskStatus.PENDING;

    private String staffNotes;
    private String supervisorComments;

    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
