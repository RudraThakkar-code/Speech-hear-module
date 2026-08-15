package com.example.clinical.controller;

import com.example.clinical.dto.CaseCreateRequest;
import com.example.clinical.dto.CaseResponse;
import com.example.clinical.dto.EncounterCreateRequest;
import com.example.clinical.dto.EncounterResponse;
import com.example.clinical.service.ClinicalCaseService;
import com.example.clinical.service.EncounterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
public class ClinicalCaseController {

    private final ClinicalCaseService caseService;
    private final EncounterService encounterService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CaseResponse createCase(@Valid @RequestBody CaseCreateRequest request) {
        return caseService.createCase(request);
    }

    @GetMapping("/{caseId}")
    public CaseResponse getCase(@PathVariable UUID caseId) {
        return caseService.getCase(caseId);
    }

    @PostMapping("/{caseId}/encounters")
    @ResponseStatus(HttpStatus.CREATED)
    public EncounterResponse createEncounterForCase(
            @PathVariable UUID caseId,
            @Valid @RequestBody EncounterCreateRequest request) {

        request.setCaseId(caseId);
        return encounterService.createEncounter(request);
    }
}
