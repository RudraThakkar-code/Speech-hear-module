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
@Getter @Setter @NoArgsConstructor
public class SpeechSample extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "sample_id", nullable = false, updatable = false) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "assignment_id", nullable = false) private AssessmentTaskAssignment assignment;
    @Column(name = "assessment_language_code", nullable = false) private String assessmentLanguageCode;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "speaker_type", nullable = false, columnDefinition = "speaker_type") private SpeakerType speakerType;
    @Column(name = "speaker_count") private Integer speakerCount;
    @Column(name = "sample_duration_seconds", nullable = false) private Integer sampleDurationSeconds;
    @Column(name = "sample_audio_url", nullable = false) private String sampleAudioUrl;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "recording_quality", nullable = false, columnDefinition = "recording_quality") private RecordingQuality recordingQuality;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "background_noise_level", nullable = false, columnDefinition = "noise_level") private NoiseLevel backgroundNoiseLevel;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "analysis_eligibility", nullable = false, columnDefinition = "analysis_eligibility") private AnalysisEligibility analysisEligibility;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "analysis_ineligibility_reason", columnDefinition = "analysis_ineligibility_reason") private AnalysisIneligibilityReason analysisIneligibilityReason;
}
