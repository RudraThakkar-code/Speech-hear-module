package com.example.clinical.dto;

import com.example.clinical.domain.enums.EarType;
import com.example.clinical.domain.enums.PatientResponse;
import com.example.clinical.domain.enums.ResponseMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class HearingMeasurementCreateRequest {

    @NotNull(message = "Trial order is required")
    @Positive(message = "Trial order must be a positive integer")
    private Integer trialOrder;

    @NotNull(message = "Ear is required")
    private EarType ear;

    @NotNull(message = "Frequency is required")
    @Positive(message = "Frequency must be a positive integer")
    private Integer frequencyPresented;

    @NotNull(message = "Intensity is required")
    @PositiveOrZero(message = "Intensity must be zero or a positive integer")
    private Integer intensityPresented;

    @NotNull(message = "Response method is required")
    private ResponseMethod responseMethod;

    @NotNull(message = "Patient response is required")
    private PatientResponse patientResponse;
}
