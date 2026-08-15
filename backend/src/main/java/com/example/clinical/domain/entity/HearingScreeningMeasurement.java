package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.EarType;
import com.example.clinical.domain.enums.PatientResponse;
import com.example.clinical.domain.enums.ResponseMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "hearing_screening_measurement")
@Getter
@Setter
@NoArgsConstructor
public class HearingScreeningMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "measurement_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id", nullable = false)
    private HearingScreening hearingScreening;

    @Column(name = "trial_order", nullable = false)
    private Integer trialOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "ear", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private EarType ear;

    @Column(name = "frequency_presented", nullable = false)
    private Integer frequencyPresented;

    @Column(name = "intensity_presented", nullable = false)
    private Integer intensityPresented;

    @Enumerated(EnumType.STRING)
    @Column(name = "response_method", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ResponseMethod responseMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "patient_response", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private PatientResponse patientResponse;
}
