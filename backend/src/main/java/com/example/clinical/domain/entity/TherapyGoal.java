package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.GoalType;
import com.example.clinical.domain.enums.TherapyDomain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "therapy_goal")
@Getter
@Setter
@NoArgsConstructor
public class TherapyGoal extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "goal_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private ClinicalCase clinicalCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id")
    private Encounter encounter;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "goal_type", nullable = false, columnDefinition = "goal_type")
    private GoalType goalType;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "domain", nullable = false, columnDefinition = "therapy_domain")
    private TherapyDomain domain;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
}
