package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.SupervisorAction;
import com.example.clinical.domain.enums.SupervisorReviewStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "supervisor_review")
@Getter
@Setter
@NoArgsConstructor
public class SupervisorReview {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "review_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supervisor_id", nullable = false)
    private User supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_clinical_interpretation_id")
    private ClinicalInterpretation clinicalInterpretation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_therapy_plan_id")
    private TherapyPlan therapyPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_therapy_session_id")
    private TherapySession therapySession;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "review_status", nullable = false, columnDefinition = "supervisor_review_status")
    private SupervisorReviewStatus reviewStatus;

    @Column(name = "reviewed_at")
    private ZonedDateTime reviewedAt;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "supervisor_action", nullable = false, columnDefinition = "supervisor_action")
    private SupervisorAction supervisorAction;

    @Column(name = "supervisor_comments", columnDefinition = "TEXT")
    private String supervisorComments;
}
