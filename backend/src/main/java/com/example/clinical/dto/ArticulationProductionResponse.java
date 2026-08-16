package com.example.clinical.dto;
import com.example.clinical.domain.enums.ArticulationAccuracy; import java.util.UUID;
public record ArticulationProductionResponse(UUID id, UUID targetId, UUID encounterId, UUID productionContextId, Integer attemptNumber, ArticulationAccuracy productionAccuracy, String producedPhoneme, String clinicianObservation, String productionAudioUrl) {}
