package com.example.clinical.controller;

import com.example.clinical.dto.HearingMeasurementCreateRequest;
import com.example.clinical.dto.HearingMeasurementResponse;
import com.example.clinical.dto.HearingScreeningCreateRequest;
import com.example.clinical.dto.HearingScreeningResponse;
import com.example.clinical.service.HearingScreeningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HearingScreeningController {

    private final HearingScreeningService hearingScreeningService;

    @PostMapping("/encounters/{encounterId}/hearing-screenings")
    @ResponseStatus(HttpStatus.CREATED)
    public HearingScreeningResponse createHearingScreening(
            @PathVariable UUID encounterId,
            @Valid @RequestBody HearingScreeningCreateRequest request) {
        return hearingScreeningService.createHearingScreening(encounterId, request);
    }

    @GetMapping("/hearing-screenings/{screeningId}")
    public HearingScreeningResponse getHearingScreening(@PathVariable UUID screeningId) {
        return hearingScreeningService.getHearingScreening(screeningId);
    }

    @PostMapping("/hearing-screenings/{screeningId}/measurements")
    @ResponseStatus(HttpStatus.CREATED)
    public HearingMeasurementResponse addMeasurement(
            @PathVariable UUID screeningId,
            @Valid @RequestBody HearingMeasurementCreateRequest request) {
        return hearingScreeningService.addMeasurement(screeningId, request);
    }
}
