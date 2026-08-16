package com.example.clinical.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_artifact")
@Getter
@Setter
@NoArgsConstructor
public class AiArtifact {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ai_artifact_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_speech_sample_id")
    private SpeechSample speechSample;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_video_observation_id")
    private VideoObservation videoObservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_clinical_interpretation_id")
    private ClinicalInterpretation clinicalInterpretation;

    @Column(name = "model_version", nullable = false)
    private String modelVersion;

    @Column(name = "analysis_type", nullable = false, columnDefinition = "ai_analysis_type")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private String analysisType;

    @Column(name = "ai_generated_result", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Object aiGeneratedResult;

    @Column(name = "confidence_score", nullable = false)
    private BigDecimal confidenceScore;

    @Column(name = "human_review_status", nullable = false, columnDefinition = "human_review_status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private String humanReviewStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private ZonedDateTime reviewedAt;
}
