package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "speech_sample")
@Getter
@Setter
@NoArgsConstructor
public class SpeechSample extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "sample_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private AssessmentTaskAssignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_language_code", nullable = false)
    private LanguageReference assessmentLanguage;

    @Enumerated(EnumType.STRING)
    @Column(name = "speaker_type", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private SpeakerType speakerType;

    @Column(name = "speaker_count")
    private Integer speakerCount;

    @Column(name = "sample_duration_seconds", nullable = false)
    private Integer sampleDurationSeconds;

    @Column(name = "sample_audio_url", nullable = false)
    private String sampleAudioUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "recording_quality", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private RecordingQuality recordingQuality;

    @Enumerated(EnumType.STRING)
    @Column(name = "background_noise_level", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private NoiseLevel backgroundNoiseLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_eligibility", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private AnalysisEligibility analysisEligibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_ineligibility_reason")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private AnalysisIneligibilityReason analysisIneligibilityReason;
}
