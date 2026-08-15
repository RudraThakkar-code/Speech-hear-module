package com.example.clinical.dto;

import com.example.clinical.domain.enums.AssessmentReason;
import com.example.clinical.domain.enums.EncounterType;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class EncounterResponse {
    private UUID id;
    private UUID caseId;
    private EncounterType encounterType;
    private ZonedDateTime encounterDateTime;
    private AssessmentReason assessmentReason;
}
