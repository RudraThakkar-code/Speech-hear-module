package com.example.clinical.service;

import com.example.clinical.domain.entity.Encounter;
import com.example.clinical.domain.entity.HearingScreening;
import com.example.clinical.domain.entity.SpeechSample;
import com.example.clinical.domain.entity.ArticulationProductionAttempt;
import com.example.clinical.domain.entity.FluencyAssessment;
import com.example.clinical.dto.EvidenceSummaryResponse;
import com.example.clinical.dto.EncounterResponse;
import com.example.clinical.dto.HearingScreeningResponse;
import com.example.clinical.dto.SpeechSampleResponse;
import com.example.clinical.dto.ArticulationProductionResponse;
import com.example.clinical.dto.FluencyAssessmentResponse;
import com.example.clinical.repository.EncounterRepository;
import com.example.clinical.repository.HearingScreeningRepository;
import com.example.clinical.repository.AssessmentTaskAssignmentRepository;
import com.example.clinical.repository.SpeechSampleRepository;
import com.example.clinical.repository.ArticulationProductionAttemptRepository;
import com.example.clinical.repository.FluencyAssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvidenceSummaryService {

    private final EncounterRepository encounterRepository;
    private final EncounterService encounterService;
    private final HearingScreeningRepository hearingScreeningRepository;
    private final SpeechSampleRepository speechSampleRepository;
    private final ArticulationProductionAttemptRepository articulationRepository;
    private final FluencyAssessmentRepository fluencyRepository;

    @Transactional(readOnly = true)
    public EvidenceSummaryResponse getEvidenceSummary(UUID encounterId) {
        Encounter encounter = encounterRepository.findById(encounterId)
                .orElseThrow(() -> new IllegalArgumentException("Encounter not found"));

        EncounterResponse encounterResponse = encounterService.getEncounter(encounterId);

        List<HearingScreening> screenings = hearingScreeningRepository.findByEncounter(encounter);
        List<HearingScreeningResponse> screeningResponses = screenings.stream()
                .map(s -> {
                    HearingScreeningResponse response = new HearingScreeningResponse();
                    response.setId(s.getId());
                    response.setEncounterId(s.getEncounter().getId());
                    response.setScreeningDate(s.getScreeningDate());
                    response.setScreeningEnvironment(s.getScreeningEnvironment());
                    response.setScreeningDeviceUsed(s.getScreeningDeviceUsed());
                    response.setScreeningOutcome(s.getScreeningOutcome());
                    response.setRecommendedAction(s.getRecommendedAction());
                    return response;
                }).collect(Collectors.toList());

        List<ArticulationProductionAttempt> articulations = articulationRepository.findByEncounter(encounter);
        List<ArticulationProductionResponse> articulationResponses = articulations.stream()
                .map(a -> new ArticulationProductionResponse(a.getId(), a.getTarget().getId(), a.getEncounter().getId(),
                        a.getProductionContext() == null ? null : a.getProductionContext().getId(),
                        a.getAttemptNumber(), a.getProductionAccuracy(), a.getProducedPhoneme(),
                        a.getClinicianObservation(), a.getProductionAudioUrl()))
                .collect(Collectors.toList());

        List<FluencyAssessment> fluencies = fluencyRepository.findByEncounter(encounter);
        List<FluencyAssessmentResponse> fluencyResponses = fluencies.stream()
                .map(a -> new FluencyAssessmentResponse(a.getId(), a.getEncounter().getId(),
                        a.getFluencySample().getId(), a.getSpeechRateObservation(), a.getSpeechRateValue(),
                        a.getSpeechRateUnit(), a.getTotalSyllables(), a.getTotalWords(), a.getRepetitionCount(),
                        a.getProlongationDurationEst(), a.getBlockDurationEst(), a.getAtypicalPauseCount()))
                .collect(Collectors.toList());

        List<SpeechSample> speechSamples = speechSampleRepository.findByEncounter(encounter);
        List<SpeechSampleResponse> speechSampleResponses = speechSamples.stream()
                .map(s -> new SpeechSampleResponse(s.getId(), s.getAssignment().getId(),
                        s.getAssessmentLanguageCode(), s.getSpeakerType(), s.getSpeakerCount(),
                        s.getSampleDurationSeconds(), s.getSampleAudioUrl(), s.getRecordingQuality(),
                        s.getBackgroundNoiseLevel(), s.getAnalysisEligibility(), s.getAnalysisIneligibilityReason()))
                .collect(Collectors.toList());

        return EvidenceSummaryResponse.builder()
                .encounter(encounterResponse)
                .hearingScreenings(screeningResponses)
                .speechSamples(speechSampleResponses)
                .articulationResults(articulationResponses)
                .fluencyResults(fluencyResponses)
                .build();
    }
}
