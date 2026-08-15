package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.AssessmentReason;
import com.example.clinical.domain.enums.EncounterType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "encounter")
@Getter
@Setter
@NoArgsConstructor
public class Encounter extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "encounter_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ClinicalCase clinicalCase;

    @Enumerated(EnumType.STRING)
    @Column(name = "encounter_type", nullable = false)
    private EncounterType encounterType;

    @Column(name = "encounter_date_time", nullable = false)
    private ZonedDateTime encounterDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_reason", nullable = false)
    private AssessmentReason assessmentReason;

    // Active history links snapshot
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_chief_complaint_id")
    private ChiefComplaint activeChiefComplaint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_family_history_id")
    private FamilyHistory activeFamilyHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_birth_history_id")
    private BirthHistory activeBirthHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_medical_history_id")
    private MedicalHistory activeMedicalHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_developmental_history_id")
    private DevelopmentalHistory activeDevelopmentalHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_language_history_id")
    private LanguageHistory activeLanguageHistory;
}
