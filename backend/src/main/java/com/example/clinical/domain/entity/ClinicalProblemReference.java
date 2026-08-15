package com.example.clinical.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clinical_problem_reference")
@Getter
@Setter
@NoArgsConstructor
public class ClinicalProblemReference {

    @Id
    @Column(name = "problem_code", length = 50, nullable = false)
    private String problemCode;

    @Column(name = "problem_name", nullable = false)
    private String problemName;
}
