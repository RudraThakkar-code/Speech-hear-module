package com.example.clinical.dto;

import com.example.clinical.domain.enums.SupervisorAction;
import com.example.clinical.domain.enums.SupervisorReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupervisorReviewResponse {
    private UUID reviewId;
    private UUID reviewedInterpretationId;
    private SupervisorReviewStatus status;
    private SupervisorAction action;
    private String comments;
    private OffsetDateTime reviewedAt;
}
