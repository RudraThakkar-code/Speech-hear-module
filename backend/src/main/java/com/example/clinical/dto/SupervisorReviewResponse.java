package com.example.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class SupervisorReviewResponse {
    private UUID reviewId;
    private UUID interpretationId;
    private String reviewStatus;
    private String action;
    private String comments;
    private UUID correctionRequestId;
}
