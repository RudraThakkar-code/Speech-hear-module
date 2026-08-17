package com.example.clinical.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "voice_objective_measurement")
@Getter
@Setter
@NoArgsConstructor
public class VoiceObjectiveMeasurement extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "measurement_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voice_sample_id", nullable = false)
    private SpeechSample voiceSample;

    @Column(name = "parameter_name", nullable = false)
    private String parameterName;

    @Column(name = "\"value\"", nullable = false)
    private BigDecimal value;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "measurement_source", nullable = false, columnDefinition = "measurement_source")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private String measurementSource;

    @Column(name = "measurement_method", nullable = false)
    private String measurementMethod;
}
