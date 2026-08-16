package com.example.clinical.dto;

import com.example.clinical.domain.enums.SpeechRateObservation;
import com.example.clinical.domain.enums.SpeechRateUnit;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class FluencyAssessmentCreateRequest {
    private UUID fluencySampleId;

    private SpeechRateObservation speechRateObservation;

    @PositiveOrZero(message = "Speech rate value must be zero or positive")
    private BigDecimal speechRateValue;

    private SpeechRateUnit speechRateUnit;

    @PositiveOrZero(message = "Total syllables must be zero or positive")
    private Integer totalSyllables;

    @PositiveOrZero(message = "Total words must be zero or positive")
    private Integer totalWords;

    @PositiveOrZero(message = "Repetition count must be zero or positive")
    private Integer repetitionCount;

    @PositiveOrZero(message = "Prolongation duration must be zero or positive")
    private BigDecimal prolongationDurationEst;

    @PositiveOrZero(message = "Block duration must be zero or positive")
    private BigDecimal blockDurationEst;

    @PositiveOrZero(message = "Atypical pause count must be zero or positive")
    private Integer atypicalPauseCount;
}
