package com.example.clinical.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CaseCreateRequest {

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @NotNull(message = "Assigned Therapist ID is required")
    private UUID assignedTherapistId;

    @NotNull(message = "Assigned Supervisor ID is required")
    private UUID assignedSupervisorId;
}
