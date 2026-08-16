package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.ArticulationAccuracy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "articulation_production_attempt")
@Getter
@Setter
@NoArgsConstructor
public class ArticulationProductionAttempt extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "production_attempt_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private ArticulationTargetLibrary target;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    private Encounter encounter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_context_id")
    private ArticulationAssessmentContext productionContext;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "production_accuracy", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ArticulationAccuracy productionAccuracy;

    @Column(name = "produced_phoneme")
    private String producedPhoneme;

    @Column(name = "clinician_observation")
    private String clinicianObservation;

    @Column(name = "production_audio_url")
    private String productionAudioUrl;
}
