package com.example.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class FollowUpResponse {
    private UUID followUpId;
    private UUID encounterId;
    private Boolean previousRecommendationsMet;
    private String currentStatusSummary;
}
