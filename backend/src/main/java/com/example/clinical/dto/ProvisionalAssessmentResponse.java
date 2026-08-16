package com.example.clinical.dto;

import com.example.clinical.domain.enums.ClinicalAssessmentStatus;
import com.example.clinical.domain.enums.ClinicalRecommendedAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvisionalAssessmentResponse {
    private UUID interpretationId;
    private String evidenceSummary;
    private ClinicalAssessmentStatus assessmentStatus;
    private ClinicalRecommendedAction recommendedAction;
    private List<ProvisionalProblemResponse> identifiedProblems;
}
