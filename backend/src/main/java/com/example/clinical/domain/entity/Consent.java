package com.example.clinical.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "consent")
@Getter
@Setter
@NoArgsConstructor
public class Consent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "consent_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "consent_version", nullable = false)
    private Integer consentVersion = 1;

    @Column(name = "consented_at", nullable = false)
    private ZonedDateTime consentedAt;

    @Column(name = "withdrawn_at")
    private ZonedDateTime withdrawnAt;

    @Column(name = "consent_data_collection", nullable = false)
    private boolean dataCollection;

    @Column(name = "consent_audio_video", nullable = false)
    private boolean audioVideo;

    @Column(name = "consent_ai_analysis", nullable = false)
    private boolean aiAnalysis;

    @Column(name = "consent_ai_training", nullable = false, columnDefinition = "consent_ai_training")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private String aiTraining;

    @Column(name = "consent_signature_url", nullable = false)
    private String signatureUrl;
}
