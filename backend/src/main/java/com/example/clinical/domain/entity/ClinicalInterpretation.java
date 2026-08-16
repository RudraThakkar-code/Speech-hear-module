package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.ClinicalAssessmentStatus;
import com.example.clinical.domain.enums.ClinicalRecommendedAction;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "clinical_interpretation")
@Getter
@Setter
public class ClinicalInterpretation extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "clinical_interpretation_id")
    private UUID clinicalInterpretationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "encounter_id", nullable = false)
    private Encounter encounter;

    @Column(name = "evidence_summary", nullable = false, columnDefinition = "TEXT")
    private String evidenceSummary;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "clinical_assessment_status", nullable = false)
    private ClinicalAssessmentStatus clinicalAssessmentStatus;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "recommended_action", nullable = false)
    private ClinicalRecommendedAction recommendedAction;

    @OneToMany(mappedBy = "interpretation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClinicalInterpretationProblem> problems = new HashSet<>();

    public void addProblem(ClinicalInterpretationProblem problem) {
        problems.add(problem);
        problem.setInterpretation(this);
    }

    public void removeProblem(ClinicalInterpretationProblem problem) {
        problems.remove(problem);
        problem.setInterpretation(null);
    }
}
