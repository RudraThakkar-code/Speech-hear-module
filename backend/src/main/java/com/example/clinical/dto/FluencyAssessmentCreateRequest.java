package com.example.clinical.dto;

import com.example.clinical.domain.enums.SpeechRateObservation;
import com.example.clinical.domain.enums.SpeechRateUnit;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class FluencyAssessmentCreateRequest {
    private UUID fluencySampleId;
    private SpeechRateObservation speechRateObservation;
    private BigDecimal speechRateValue;
    private SpeechRateUnit speechRateUnit;
    private Integer totalSyllables;
    private Integer totalWords;
    private Integer repetitionCount;
    private BigDecimal prolongationDurationEst;
    private BigDecimal blockDurationEst;
    private Integer atypicalPauseCount;
}
