package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.ClinicalAssessmentStatus;
import com.example.clinical.domain.enums.ClinicalRecommendedAction;
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
@Table(name = "clinical_interpretation_history")
@Getter
@Setter
public class ClinicalInterpretationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id")
    private UUID historyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interpretation_id", nullable = false)
    private ClinicalInterpretation interpretation;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "evidence_summary", nullable = false, columnDefinition = "TEXT")
    private String evidenceSummary;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "clinical_assessment_status", nullable = false)
    private ClinicalAssessmentStatus clinicalAssessmentStatus;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "recommended_action", nullable = false)
    private ClinicalRecommendedAction recommendedAction;

    @Column(name = "created_by")
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
