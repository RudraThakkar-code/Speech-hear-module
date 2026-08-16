package com.example.clinical.controller;

import com.example.clinical.dto.ClinicalInterpretationRequest;
import com.example.clinical.dto.ClinicalInterpretationResponse;
import com.example.clinical.dto.ProvisionalAssessmentRequest;
import com.example.clinical.dto.ProvisionalAssessmentResponse;
import com.example.clinical.service.ClinicalInterpretationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ClinicalInterpretationController {

    private final ClinicalInterpretationService interpretationService;

    @PostMapping("/encounters/{encounterId}/clinical-interpretations")
    public ResponseEntity<ClinicalInterpretationResponse> createInterpretation(
            @PathVariable UUID encounterId,
            @Valid @RequestBody ClinicalInterpretationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interpretationService.createInterpretation(encounterId, request));
    }

    @GetMapping("/clinical-interpretations/{interpretationId}")
    public ResponseEntity<ClinicalInterpretationResponse> getInterpretation(
            @PathVariable UUID interpretationId) {
        return ResponseEntity.ok(interpretationService.getInterpretation(interpretationId));
    }

    @PostMapping("/clinical-interpretations/{interpretationId}/provisional-assessment")
    public ResponseEntity<ProvisionalAssessmentResponse> recordProvisionalAssessment(
            @PathVariable UUID interpretationId,
            @Valid @RequestBody ProvisionalAssessmentRequest request) {
        return ResponseEntity.ok(interpretationService.recordProvisionalAssessment(interpretationId, request));
    }
}
