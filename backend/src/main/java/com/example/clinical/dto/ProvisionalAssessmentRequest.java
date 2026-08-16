package com.example.clinical.dto;

import com.example.clinical.domain.enums.ClinicalRecommendedAction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvisionalAssessmentRequest {
    @Valid
    @NotEmpty
    private List<ProvisionalProblemRequest> problems;

    @NotNull
    private ClinicalRecommendedAction recommendedAction;
}
