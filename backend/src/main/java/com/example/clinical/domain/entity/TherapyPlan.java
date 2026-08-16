package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.FrequencyUnit;
import com.example.clinical.domain.enums.SupervisorApprovalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "therapy_plan")
@Getter
@Setter
@NoArgsConstructor
public class TherapyPlan extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "plan_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private ClinicalCase clinicalCase;

    @Column(name = "plan_start_date", nullable = false)
    private LocalDate planStartDate;

    @Column(name = "plan_end_date")
    private LocalDate planEndDate;

    @Column(name = "planned_activities", nullable = false, columnDefinition = "TEXT")
    private String plannedActivities;

    @Column(name = "frequency_value", nullable = false)
    private Integer frequencyValue;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "frequency_unit", nullable = false, columnDefinition = "frequency_unit")
    private FrequencyUnit frequencyUnit;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "supervisor_approval_status", nullable = false, columnDefinition = "supervisor_approval_status")
    private SupervisorApprovalStatus supervisorApprovalStatus;

    @ManyToMany
    @JoinTable(name = "therapy_plan_goal",
        joinColumns = @JoinColumn(name = "plan_id"),
        inverseJoinColumns = @JoinColumn(name = "goal_id"))
    private Set<TherapyGoal> goals = new HashSet<>();
}
