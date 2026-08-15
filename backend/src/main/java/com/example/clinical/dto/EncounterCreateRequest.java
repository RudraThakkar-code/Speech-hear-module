package com.example.clinical.dto;

import com.example.clinical.domain.enums.AssessmentReason;
import com.example.clinical.domain.enums.EncounterType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class EncounterCreateRequest {

    @JsonIgnore
    private UUID caseId;

    @NotNull(message = "Encounter Type is required")
    private EncounterType encounterType;

    @NotNull(message = "Encounter Date and Time is required")
    private ZonedDateTime encounterDateTime;

    @NotNull(message = "Assessment Reason is required")
    private AssessmentReason assessmentReason;
}
