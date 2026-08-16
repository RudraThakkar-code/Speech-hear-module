package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.AssignmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "assessment_task_assignment")
@Getter
@Setter
@NoArgsConstructor
public class AssessmentTaskAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "assignment_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private AssessmentTaskLibrary task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    private Encounter encounter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", nullable = false)
    private User assignedBy;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    @Column(name = "required", nullable = false)
    private Boolean required;

    @Enumerated(EnumType.STRING)
    @Column(name = "completion_status", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private AssignmentStatus completionStatus;
}
