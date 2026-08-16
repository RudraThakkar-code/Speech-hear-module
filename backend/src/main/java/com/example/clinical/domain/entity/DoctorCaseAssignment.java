package com.example.clinical.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "doctor_case_assignment")
@Getter
@Setter
@NoArgsConstructor
public class DoctorCaseAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "assignment_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private ClinicalCase clinicalCase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @Column(name = "assigned_at", nullable = false)
    private ZonedDateTime assignedAt;

    @Column(name = "ended_at")
    private ZonedDateTime endedAt;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
}
