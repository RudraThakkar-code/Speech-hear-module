package com.example.clinical.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "voice_assessment")
@Getter
@Setter
@NoArgsConstructor
public class VoiceAssessment extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "voice_assessment_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "encounter_id", nullable = false)
    private Encounter encounter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voice_sample_id", nullable = false)
    private SpeechSample voiceSample;

    @Column(name = "pitch_observation", nullable = false, columnDefinition = "pitch_observation")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private String pitchObservation;

    @Column(name = "loudness_observation", nullable = false, columnDefinition = "loudness_observation")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private String loudnessObservation;

    @Column(name = "voice_quality", nullable = false, columnDefinition = "voice_quality[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private String[] voiceQuality;

    @Column(name = "resonance_observation", nullable = false, columnDefinition = "resonance_observation")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private String resonanceObservation;

    @Column(name = "voice_concern_flag", nullable = false)
    private boolean voiceConcernFlag;
}
