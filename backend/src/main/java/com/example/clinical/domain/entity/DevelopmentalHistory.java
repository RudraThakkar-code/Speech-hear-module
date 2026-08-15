package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.MotorMilestonesStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;


import java.util.UUID;

@Entity
@Table(name = "developmental_history")
@Getter
@Setter
@NoArgsConstructor
public class DevelopmentalHistory extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ClinicalCase clinicalCase;

    @Column(name = "is_current", nullable = false)
    private boolean isCurrent = true;

    @Column(name = "milestone_sitting_age")
    private Integer milestoneSittingAge;

    @Column(name = "milestone_walking_age")
    private Integer milestoneWalkingAge;

    @Enumerated(EnumType.STRING)
    @Column(name = "motor_milestones_status", nullable = false, columnDefinition = "motor_milestones_status")
    @JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    private MotorMilestonesStatus motorMilestonesStatus;

    @Column(name = "motor_milestones_details")
    private String motorMilestonesDetails;

    @Column(name = "feeding_difficulties", nullable = false)
    private boolean feedingDifficulties;

    @Column(name = "scholastic_history")
    private String scholasticHistory;
}
