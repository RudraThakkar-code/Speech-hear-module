package com.example.clinical.dto;

import com.example.clinical.domain.enums.ArticulationAccuracy;
import lombok.Data;

import java.util.UUID;

@Data
public class ArticulationAttemptResponse {
    private UUID id;
    private UUID targetId;
    private UUID encounterId;
    private UUID productionContextId;
    private Integer attemptNumber;
    private ArticulationAccuracy productionAccuracy;
    private String producedPhoneme;
    private String clinicianObservation;
    private String productionAudioUrl;
}
