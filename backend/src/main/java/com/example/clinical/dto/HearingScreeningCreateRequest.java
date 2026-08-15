package com.example.clinical.dto;

import com.example.clinical.domain.enums.HearingRecommendedAction;
import com.example.clinical.domain.enums.ScreeningEnvironment;
import com.example.clinical.domain.enums.ScreeningOutcome;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HearingScreeningCreateRequest {

    @NotNull(message = "Screening Date is required")
    @PastOrPresent(message = "Screening Date cannot be in the future")
    private LocalDate screeningDate;

    @NotNull(message = "Screening Environment is required")
    private ScreeningEnvironment screeningEnvironment;

    @NotBlank(message = "Screening Device Used is required")
    private String screeningDeviceUsed;

    @NotNull(message = "Screening Outcome is required")
    private ScreeningOutcome screeningOutcome;

    @NotNull(message = "Recommended Action is required")
    private HearingRecommendedAction recommendedAction;
}
