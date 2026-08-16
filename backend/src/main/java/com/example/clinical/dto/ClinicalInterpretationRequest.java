package com.example.clinical.dto;

import com.example.clinical.domain.enums.ClinicalAssessmentStatus;
import com.example.clinical.domain.enums.ClinicalRecommendedAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalInterpretationRequest {
    @NotBlank
    private String evidenceSummary;

    @NotNull
    private ClinicalAssessmentStatus clinicalAssessmentStatus;

    @NotNull
    private ClinicalRecommendedAction recommendedAction;
}
