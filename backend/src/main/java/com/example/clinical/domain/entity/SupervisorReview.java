package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.SupervisorAction;
import com.example.clinical.domain.enums.SupervisorReviewStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "supervisor_review")
@Getter
@Setter
public class SupervisorReview extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "review_id")
    private UUID reviewId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supervisor_id", nullable = false)
    private User supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_clinical_interpretation_id")
    private ClinicalInterpretation reviewedClinicalInterpretation;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "review_status", nullable = false)
    private SupervisorReviewStatus reviewStatus;

    @Column(name = "reviewed_at")
    private OffsetDateTime reviewedAt;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "supervisor_action", nullable = false)
    private SupervisorAction supervisorAction;

    @Column(name = "supervisor_comments", columnDefinition = "TEXT")
    private String supervisorComments;
}
