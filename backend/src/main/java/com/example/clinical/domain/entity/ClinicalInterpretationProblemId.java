package com.example.clinical.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ClinicalInterpretationProblemId implements Serializable {

    @Column(name = "clinical_interpretation_id")
    private UUID clinicalInterpretationId;

    @Column(name = "problem_code")
    private String problemCode;
}
