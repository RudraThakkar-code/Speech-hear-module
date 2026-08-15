package com.example.clinical.dto;

import com.example.clinical.domain.enums.EarType;
import com.example.clinical.domain.enums.PatientResponse;
import com.example.clinical.domain.enums.ResponseMethod;
import lombok.Data;

import java.util.UUID;

@Data
public class HearingMeasurementResponse {
    private UUID id;
    private UUID screeningId;
    private Integer trialOrder;
    private EarType ear;
    private Integer frequencyPresented;
    private Integer intensityPresented;
    private ResponseMethod responseMethod;
    private PatientResponse patientResponse;
}
