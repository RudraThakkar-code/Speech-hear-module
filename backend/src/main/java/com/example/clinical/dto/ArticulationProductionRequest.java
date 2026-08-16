package com.example.clinical.dto;
import com.example.clinical.domain.enums.ArticulationAccuracy; import jakarta.validation.constraints.Min; import jakarta.validation.constraints.NotNull; import java.util.UUID;
public record ArticulationProductionRequest(@NotNull UUID targetId,@NotNull UUID encounterId,UUID productionContextId,@NotNull @Min(1) Integer attemptNumber,@NotNull ArticulationAccuracy productionAccuracy,String producedPhoneme,String clinicianObservation,String productionAudioUrl) {}
