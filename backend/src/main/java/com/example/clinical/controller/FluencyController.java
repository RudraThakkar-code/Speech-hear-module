package com.example.clinical.controller;

import com.example.clinical.dto.FluencyAssessmentCreateRequest;
import com.example.clinical.dto.FluencyAssessmentResponse;
import com.example.clinical.service.FluencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/encounters/{encounterId}/fluency")
@RequiredArgsConstructor
public class FluencyController {

    private final FluencyService fluencyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FluencyAssessmentResponse createFluencyAssessment(
            @PathVariable UUID encounterId,
            @Valid @RequestBody FluencyAssessmentCreateRequest request) {
        return fluencyService.createFluencyAssessment(encounterId, request);
    }

    @GetMapping
    public List<FluencyAssessmentResponse> getFluencyAssessments(@PathVariable UUID encounterId) {
        return fluencyService.getFluencyAssessmentsByEncounter(encounterId);
    }
}
