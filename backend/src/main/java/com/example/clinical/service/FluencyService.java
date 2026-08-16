package com.example.clinical.service;

import com.example.clinical.domain.entity.Encounter;
import com.example.clinical.domain.entity.FluencyAssessment;
import com.example.clinical.domain.entity.SpeechSample;
import com.example.clinical.dto.FluencyAssessmentCreateRequest;
import com.example.clinical.dto.FluencyAssessmentResponse;
import com.example.clinical.repository.EncounterRepository;
import com.example.clinical.repository.FluencyAssessmentRepository;
import com.example.clinical.repository.SpeechSampleRepository;
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
public class FluencyService {

    private final FluencyAssessmentRepository fluencyRepository;
    private final EncounterRepository encounterRepository;
    private final SpeechSampleRepository speechSampleRepository;

    @Transactional
    public FluencyAssessmentResponse createFluencyAssessment(UUID encounterId, FluencyAssessmentCreateRequest request) {
        Encounter encounter = encounterRepository.findById(encounterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encounter not found"));

        SpeechSample sample = null;
        try {
            if (request.getFluencySampleId() != null) {
                sample = speechSampleRepository.findById(request.getFluencySampleId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Speech Sample not found"));
            }
        } catch (IllegalArgumentException e) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID");
        }

        FluencyAssessment assessment = new FluencyAssessment();
        assessment.setEncounter(encounter);
        assessment.setFluencySample(sample);
        assessment.setSpeechRateObservation(request.getSpeechRateObservation());
        assessment.setSpeechRateValue(request.getSpeechRateValue());
        assessment.setSpeechRateUnit(request.getSpeechRateUnit());
        assessment.setTotalSyllables(request.getTotalSyllables());
        assessment.setTotalWords(request.getTotalWords());
        assessment.setRepetitionCount(request.getRepetitionCount());
        assessment.setProlongationDurationEst(request.getProlongationDurationEst());
        assessment.setBlockDurationEst(request.getBlockDurationEst());
        assessment.setAtypicalPauseCount(request.getAtypicalPauseCount());

        FluencyAssessment savedAssessment = fluencyRepository.save(assessment);
        return mapToResponse(savedAssessment);
    }

    public List<FluencyAssessmentResponse> getFluencyAssessmentsByEncounter(UUID encounterId) {
        return fluencyRepository.findByEncounterId(encounterId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private FluencyAssessmentResponse mapToResponse(FluencyAssessment assessment) {
        FluencyAssessmentResponse response = new FluencyAssessmentResponse();
        response.setId(assessment.getId());
        response.setEncounterId(assessment.getEncounter().getId());
        if (assessment.getFluencySample() != null) {
             response.setFluencySampleId(assessment.getFluencySample().getId());
        }
        response.setSpeechRateObservation(assessment.getSpeechRateObservation());
        response.setSpeechRateValue(assessment.getSpeechRateValue());
        response.setSpeechRateUnit(assessment.getSpeechRateUnit());
        response.setTotalSyllables(assessment.getTotalSyllables());
        response.setTotalWords(assessment.getTotalWords());
        response.setRepetitionCount(assessment.getRepetitionCount());
        response.setProlongationDurationEst(assessment.getProlongationDurationEst());
        response.setBlockDurationEst(assessment.getBlockDurationEst());
        response.setAtypicalPauseCount(assessment.getAtypicalPauseCount());
        return response;
    }
}
