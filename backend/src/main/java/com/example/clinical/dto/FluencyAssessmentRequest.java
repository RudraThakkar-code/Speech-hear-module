package com.example.clinical.dto;
import com.example.clinical.domain.enums.*; import jakarta.validation.constraints.NotNull; import java.math.BigDecimal; import java.util.UUID;
public record FluencyAssessmentRequest(@NotNull UUID encounterId,@NotNull UUID fluencySampleId,@NotNull SpeechRateObservation speechRateObservation,BigDecimal speechRateValue,SpeechRateUnit speechRateUnit,Integer totalSyllables,Integer totalWords,Integer repetitionCount,BigDecimal prolongationDurationEst,BigDecimal blockDurationEst,Integer atypicalPauseCount) {}
