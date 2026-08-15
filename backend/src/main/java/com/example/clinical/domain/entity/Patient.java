package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.DataClassification;
import com.example.clinical.domain.enums.DataProvenance;
import com.example.clinical.domain.enums.SexAtBirth;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "patient")

@Getter
@Setter
@NoArgsConstructor
public class Patient extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "patient_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "legal_name", nullable = false)
    private String legalName;

    @Column(name = "preferred_name")
    private String preferredName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex_at_birth")
    private SexAtBirth sexAtBirth;

    @Column(name = "gender_identity")
    private String genderIdentity;

    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "home_address")
    private String homeAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_provenance")
    private DataProvenance dataProvenance;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_classification")
    private DataClassification dataClassification;
}
