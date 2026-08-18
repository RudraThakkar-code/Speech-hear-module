package com.example.clinical.domain.entity;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.clinical.domain.enums.LoudnessObservation;
import com.example.clinical.domain.enums.PitchObservation;
import com.example.clinical.domain.enums.ResonanceObservation;
import com.example.clinical.domain.enums.VoiceQuality;

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
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "pitch_observation", nullable = false, columnDefinition = "pitch_observation")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private PitchObservation pitchObservation;

    @Enumerated(EnumType.STRING)
    @Column(name = "loudness_observation", nullable = false, columnDefinition = "loudness_observation")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private LoudnessObservation loudnessObservation;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "voice_quality", columnDefinition = "voice_quality[]", nullable = false)
    private VoiceQuality[] voiceQuality;

    @Enumerated(EnumType.STRING)
    @Column(name = "resonance_observation", nullable = false, columnDefinition = "resonance_observation")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ResonanceObservation resonanceObservation;

    @Column(name = "voice_concern_flag", nullable = false)
    private boolean voiceConcernFlag;
}
