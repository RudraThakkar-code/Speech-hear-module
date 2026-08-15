package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.CaseStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;


import java.util.UUID;

@Entity
@Table(name = "clinical_case")
@Getter
@Setter
@NoArgsConstructor
public class ClinicalCase extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "case_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_status", nullable = false, columnDefinition = "case_status")
    @JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    private CaseStatus caseStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_therapist_id", nullable = false)
    private User assignedTherapist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_supervisor_id", nullable = false)
    private User assignedSupervisor;
}
