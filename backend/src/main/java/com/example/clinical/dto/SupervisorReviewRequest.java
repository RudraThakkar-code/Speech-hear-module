package com.example.clinical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SupervisorReviewRequest {
    @NotNull
    private UUID supervisorId;

    @NotBlank
    private String action;

    private String comments;
    private String correctionReason;
}
