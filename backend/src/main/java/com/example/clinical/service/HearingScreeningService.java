package com.example.clinical.service;

import com.example.clinical.domain.entity.Encounter;
import com.example.clinical.domain.entity.HearingScreening;
import com.example.clinical.domain.entity.HearingScreeningMeasurement;
import com.example.clinical.dto.HearingMeasurementCreateRequest;
import com.example.clinical.dto.HearingMeasurementResponse;
import com.example.clinical.dto.HearingScreeningCreateRequest;
import com.example.clinical.dto.HearingScreeningResponse;
import com.example.clinical.repository.EncounterRepository;
import com.example.clinical.repository.HearingScreeningMeasurementRepository;
import com.example.clinical.repository.HearingScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HearingScreeningService {

    private final HearingScreeningRepository hearingScreeningRepository;
    private final HearingScreeningMeasurementRepository measurementRepository;
    private final EncounterRepository encounterRepository;

    @Transactional
    public HearingScreeningResponse createHearingScreening(UUID encounterId, HearingScreeningCreateRequest request) {
        Encounter encounter = encounterRepository.findById(encounterId)
                .orElseThrow(() -> new IllegalArgumentException("Encounter not found"));

        HearingScreening screening = new HearingScreening();
        screening.setEncounter(encounter);
        screening.setScreeningDate(request.getScreeningDate());
        screening.setScreeningEnvironment(request.getScreeningEnvironment());
        screening.setScreeningDeviceUsed(request.getScreeningDeviceUsed());
        screening.setScreeningOutcome(request.getScreeningOutcome());
        screening.setRecommendedAction(request.getRecommendedAction());

        HearingScreening saved = hearingScreeningRepository.save(screening);
        return mapToScreeningResponse(saved);
    }

    @Transactional(readOnly = true)
    public HearingScreeningResponse getHearingScreening(UUID screeningId) {
        HearingScreening screening = hearingScreeningRepository.findById(screeningId)
                .orElseThrow(() -> new IllegalArgumentException("Hearing Screening not found"));
        return mapToScreeningResponse(screening);
    }

    @Transactional
    public HearingMeasurementResponse addMeasurement(UUID screeningId, HearingMeasurementCreateRequest request) {
        HearingScreening screening = hearingScreeningRepository.findById(screeningId)
                .orElseThrow(() -> new IllegalArgumentException("Hearing Screening not found"));

        HearingScreeningMeasurement measurement = new HearingScreeningMeasurement();
        measurement.setHearingScreening(screening);
        measurement.setTrialOrder(request.getTrialOrder());
        measurement.setEar(request.getEar());
        measurement.setFrequencyPresented(request.getFrequencyPresented());
        measurement.setIntensityPresented(request.getIntensityPresented());
        measurement.setResponseMethod(request.getResponseMethod());
        measurement.setPatientResponse(request.getPatientResponse());

        HearingScreeningMeasurement saved = measurementRepository.save(measurement);
        return mapToMeasurementResponse(saved);
    }

    private HearingScreeningResponse mapToScreeningResponse(HearingScreening screening) {
        HearingScreeningResponse response = new HearingScreeningResponse();
        response.setId(screening.getId());
        response.setEncounterId(screening.getEncounter().getId());
        response.setScreeningDate(screening.getScreeningDate());
        response.setScreeningEnvironment(screening.getScreeningEnvironment());
        response.setScreeningDeviceUsed(screening.getScreeningDeviceUsed());
        response.setScreeningOutcome(screening.getScreeningOutcome());
        response.setRecommendedAction(screening.getRecommendedAction());
        return response;
    }

    private HearingMeasurementResponse mapToMeasurementResponse(HearingScreeningMeasurement measurement) {
        HearingMeasurementResponse response = new HearingMeasurementResponse();
        response.setId(measurement.getId());
        response.setScreeningId(measurement.getHearingScreening().getId());
        response.setTrialOrder(measurement.getTrialOrder());
        response.setEar(measurement.getEar());
        response.setFrequencyPresented(measurement.getFrequencyPresented());
        response.setIntensityPresented(measurement.getIntensityPresented());
        response.setResponseMethod(measurement.getResponseMethod());
        response.setPatientResponse(measurement.getPatientResponse());
        return response;
    }
}
