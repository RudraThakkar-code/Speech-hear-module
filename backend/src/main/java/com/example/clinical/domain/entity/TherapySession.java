package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "therapy_session")
@Getter
@Setter
@NoArgsConstructor
public class TherapySession extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "session_id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "encounter_id", nullable = false, unique = true)
    private Encounter encounter;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "session_status", nullable = false, columnDefinition = "session_status")
    private SessionStatus sessionStatus;

    @Column(name = "activities_performed", nullable = false, columnDefinition = "TEXT")
    private String activitiesPerformed;

    @Column(name = "patient_performance", nullable = false, columnDefinition = "TEXT")
    private String patientPerformance;

    @Column(name = "homework_assigned", columnDefinition = "TEXT")
    private String homeworkAssigned;
}
