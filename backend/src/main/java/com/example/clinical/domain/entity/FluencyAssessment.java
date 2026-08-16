package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.SpeechRateObservation;
import com.example.clinical.domain.enums.SpeechRateUnit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "fluency_assessment")
@Getter
@Setter
@NoArgsConstructor
public class FluencyAssessment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "fluency_assessment_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    private Encounter encounter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fluency_sample_id", nullable = false)
    private SpeechSample fluencySample;

    @Enumerated(EnumType.STRING)
    @Column(name = "speech_rate_observation", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private SpeechRateObservation speechRateObservation;

    @Column(name = "speech_rate_value")
    private BigDecimal speechRateValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "speech_rate_unit")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private SpeechRateUnit speechRateUnit;

    @Column(name = "total_syllables")
    private Integer totalSyllables;

    @Column(name = "total_words")
    private Integer totalWords;

    @Column(name = "repetition_count")
    private Integer repetitionCount;

    @Column(name = "prolongation_duration_est")
    private BigDecimal prolongationDurationEst;

    @Column(name = "block_duration_est")
    private BigDecimal blockDurationEst;

    @Column(name = "atypical_pause_count")
    private Integer atypicalPauseCount;
}
