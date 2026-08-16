package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.SupervisorAction;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "supervisor_review_history")
@Getter
@Setter
public class SupervisorReviewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id")
    private UUID historyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supervisor_review_id", nullable = false)
    private SupervisorReview supervisorReview;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interpretation_id", nullable = false)
    private ClinicalInterpretation interpretation;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "action", nullable = false)
    private SupervisorAction action;

    @Column(name = "review_comments", columnDefinition = "TEXT")
    private String reviewComments;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @CreationTimestamp
    @Column(name = "reviewed_at", nullable = false, updatable = false)
    private OffsetDateTime reviewedAt;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;
}
