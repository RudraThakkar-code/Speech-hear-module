package com.example.clinical.domain.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clinical_interpretation_problem")
@Getter
@Setter
@NoArgsConstructor
public class ClinicalInterpretationProblem {

    @EmbeddedId
    private ClinicalInterpretationProblemId id = new ClinicalInterpretationProblemId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clinicalInterpretationId")
    @JoinColumn(name = "clinical_interpretation_id", nullable = false)
    private ClinicalInterpretation interpretation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("problemCode")
    @JoinColumn(name = "problem_code", nullable = false)
    private ClinicalProblemReference problem;

    public ClinicalInterpretationProblem(ClinicalInterpretation interpretation, ClinicalProblemReference problem) {
        this.interpretation = interpretation;
        this.problem = problem;
        if (interpretation != null && problem != null) {
            this.id = new ClinicalInterpretationProblemId(interpretation.getClinicalInterpretationId(), problem.getProblemCode());
        }
    }
}
