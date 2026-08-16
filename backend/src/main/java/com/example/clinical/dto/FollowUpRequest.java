package com.example.clinical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FollowUpRequest {
    @NotNull
    private Boolean previousRecommendationsMet;

    @NotBlank
    private String currentStatusSummary;
}
