package com.example.clinical.dto;
import com.example.clinical.domain.enums.*; import java.math.BigDecimal; import java.util.UUID;
public record FluencyAssessmentResponse(UUID id, UUID encounterId, UUID fluencySampleId, SpeechRateObservation speechRateObservation, BigDecimal speechRateValue, SpeechRateUnit speechRateUnit, Integer totalSyllables, Integer totalWords, Integer repetitionCount, BigDecimal prolongationDurationEst, BigDecimal blockDurationEst, Integer atypicalPauseCount) {}
