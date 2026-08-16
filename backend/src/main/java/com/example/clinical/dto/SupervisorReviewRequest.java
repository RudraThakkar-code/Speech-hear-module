package com.example.clinical.dto;

import com.example.clinical.domain.enums.SupervisorAction;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupervisorReviewRequest {
    @NotNull
    private UUID supervisorId;

    @NotNull
    private SupervisorAction action;

    private String comments;
}
