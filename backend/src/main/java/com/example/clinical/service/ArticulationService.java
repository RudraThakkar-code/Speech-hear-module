package com.example.clinical.service;

import com.example.clinical.domain.entity.ArticulationAssessmentContext;
import com.example.clinical.domain.entity.ArticulationProductionAttempt;
import com.example.clinical.domain.entity.ArticulationTargetLibrary;
import com.example.clinical.domain.entity.Encounter;
import com.example.clinical.dto.ArticulationAttemptCreateRequest;
import com.example.clinical.dto.ArticulationAttemptResponse;
import com.example.clinical.dto.ArticulationContextCreateRequest;
import com.example.clinical.dto.ArticulationContextResponse;
import com.example.clinical.repository.ArticulationAssessmentContextRepository;
import com.example.clinical.repository.ArticulationProductionAttemptRepository;
import com.example.clinical.repository.ArticulationTargetLibraryRepository;
import com.example.clinical.repository.EncounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticulationService {

    private final ArticulationAssessmentContextRepository contextRepository;
    private final ArticulationProductionAttemptRepository attemptRepository;
    private final ArticulationTargetLibraryRepository targetRepository;
    private final EncounterRepository encounterRepository;

    @Transactional
    public ArticulationContextResponse createContext(UUID encounterId, ArticulationContextCreateRequest request) {
        Encounter encounter = encounterRepository.findById(encounterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encounter not found"));

        ArticulationAssessmentContext context = new ArticulationAssessmentContext();
        context.setEncounter(encounter);
        context.setContextDescription(request.getContextDescription());

        ArticulationAssessmentContext savedContext = contextRepository.save(context);
        return mapContextToResponse(savedContext);
    }

    @Transactional
    public ArticulationAttemptResponse createAttempt(UUID encounterId, ArticulationAttemptCreateRequest request) {
        Encounter encounter = encounterRepository.findById(encounterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encounter not found"));

        ArticulationTargetLibrary target = targetRepository.findById(request.getTargetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target not found"));

        ArticulationAssessmentContext context = null;
        if (request.getProductionContextId() != null) {
            context = contextRepository.findById(request.getProductionContextId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Context not found"));
        }

        ArticulationProductionAttempt attempt = new ArticulationProductionAttempt();
        attempt.setEncounter(encounter);
        attempt.setTarget(target);
        attempt.setProductionContext(context);
        attempt.setAttemptNumber(request.getAttemptNumber());
        attempt.setProductionAccuracy(request.getProductionAccuracy());
        attempt.setProducedPhoneme(request.getProducedPhoneme());
        attempt.setClinicianObservation(request.getClinicianObservation());
        attempt.setProductionAudioUrl(request.getProductionAudioUrl());

        ArticulationProductionAttempt savedAttempt = attemptRepository.save(attempt);
        return mapAttemptToResponse(savedAttempt);
    }

    public List<ArticulationAttemptResponse> getAttemptsByEncounter(UUID encounterId) {
        return attemptRepository.findByEncounterId(encounterId).stream()
                .map(this::mapAttemptToResponse)
                .collect(Collectors.toList());
    }

    private ArticulationContextResponse mapContextToResponse(ArticulationAssessmentContext context) {
        ArticulationContextResponse response = new ArticulationContextResponse();
        response.setId(context.getId());
        response.setEncounterId(context.getEncounter().getId());
        response.setContextDescription(context.getContextDescription());
        return response;
    }

    private ArticulationAttemptResponse mapAttemptToResponse(ArticulationProductionAttempt attempt) {
        ArticulationAttemptResponse response = new ArticulationAttemptResponse();
        response.setId(attempt.getId());
        response.setTargetId(attempt.getTarget().getId());
        response.setEncounterId(attempt.getEncounter().getId());
        if (attempt.getProductionContext() != null) {
            response.setProductionContextId(attempt.getProductionContext().getId());
        }
        response.setAttemptNumber(attempt.getAttemptNumber());
        response.setProductionAccuracy(attempt.getProductionAccuracy());
        response.setProducedPhoneme(attempt.getProducedPhoneme());
        response.setClinicianObservation(attempt.getClinicianObservation());
        response.setProductionAudioUrl(attempt.getProductionAudioUrl());
        return response;
    }
}
