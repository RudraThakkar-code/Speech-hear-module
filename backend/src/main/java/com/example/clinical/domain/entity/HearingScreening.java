package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.HearingRecommendedAction;
import com.example.clinical.domain.enums.ScreeningEnvironment;
import com.example.clinical.domain.enums.ScreeningOutcome;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "hearing_screening")
@Getter
@Setter
@NoArgsConstructor
public class HearingScreening extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "screening_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    private Encounter encounter;

    @Column(name = "screening_date", nullable = false)
    private LocalDate screeningDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "screening_environment", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ScreeningEnvironment screeningEnvironment;

    @Column(name = "screening_device_used", nullable = false)
    private String screeningDeviceUsed;

    @Enumerated(EnumType.STRING)
    @Column(name = "screening_outcome", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ScreeningOutcome screeningOutcome;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommended_action", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private HearingRecommendedAction recommendedAction;

    @Column(name = "existing_audiology_report_id")
    private UUID existingAudiologyReportId;
}
