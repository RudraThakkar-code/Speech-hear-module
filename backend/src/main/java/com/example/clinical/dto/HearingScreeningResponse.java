package com.example.clinical.dto;

import com.example.clinical.domain.enums.HearingRecommendedAction;
import com.example.clinical.domain.enums.ScreeningEnvironment;
import com.example.clinical.domain.enums.ScreeningOutcome;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class HearingScreeningResponse {
    private UUID id;
    private UUID encounterId;
    private LocalDate screeningDate;
    private ScreeningEnvironment screeningEnvironment;
    private String screeningDeviceUsed;
    private ScreeningOutcome screeningOutcome;
    private HearingRecommendedAction recommendedAction;
}
