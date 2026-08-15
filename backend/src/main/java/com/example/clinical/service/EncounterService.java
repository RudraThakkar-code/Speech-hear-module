package com.example.clinical.service;

import com.example.clinical.domain.entity.ClinicalCase;
import com.example.clinical.domain.entity.Encounter;
import com.example.clinical.dto.EncounterCreateRequest;
import com.example.clinical.dto.EncounterResponse;
import com.example.clinical.repository.ClinicalCaseRepository;
import com.example.clinical.repository.EncounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EncounterService {

    private final EncounterRepository encounterRepository;
    private final ClinicalCaseRepository clinicalCaseRepository;

    @Transactional
    public EncounterResponse createEncounter(EncounterCreateRequest request) {
        ClinicalCase clinicalCase = clinicalCaseRepository.findById(request.getCaseId())
                .orElseThrow(() -> new IllegalArgumentException("Case not found"));

        Encounter encounter = new Encounter();
        encounter.setClinicalCase(clinicalCase);
        encounter.setEncounterType(request.getEncounterType());
        encounter.setEncounterDateTime(request.getEncounterDateTime());
        encounter.setAssessmentReason(request.getAssessmentReason());

        Encounter saved = encounterRepository.save(encounter);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public EncounterResponse getEncounter(UUID encounterId) {
        Encounter encounter = encounterRepository.findById(encounterId)
                .orElseThrow(() -> new IllegalArgumentException("Encounter not found"));
        return mapToResponse(encounter);
    }

    private EncounterResponse mapToResponse(Encounter encounter) {
        EncounterResponse response = new EncounterResponse();
        response.setId(encounter.getId());
        response.setCaseId(encounter.getClinicalCase().getId());
        response.setEncounterType(encounter.getEncounterType());
        response.setEncounterDateTime(encounter.getEncounterDateTime());
        response.setAssessmentReason(encounter.getAssessmentReason());
        return response;
    }
}
