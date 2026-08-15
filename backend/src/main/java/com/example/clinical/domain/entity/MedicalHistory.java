package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.DataProvenance;
import com.example.clinical.domain.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "medical_history")
@Getter
@Setter
@NoArgsConstructor
public class MedicalHistory extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ClinicalCase clinicalCase;

    @Column(name = "is_current", nullable = false)
    private boolean isCurrent = true;

    @Column(name = "reported_conditions")
    private String reportedConditions;

    @Column(name = "neurological_history")
    private String neurologicalHistory;

    @Column(name = "current_medications")
    private String currentMedications;

    @Column(name = "surgical_history")
    private String surgicalHistory;

    @Column(name = "previous_therapy_history", nullable = false)
    private boolean previousTherapyHistory;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_provenance")
    private DataProvenance dataProvenance;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    private VerificationStatus verificationStatus;
}
