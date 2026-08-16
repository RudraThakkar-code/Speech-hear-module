package com.example.clinical.service;

import com.example.clinical.domain.entity.AssessmentTaskAssignment;
import com.example.clinical.domain.entity.LanguageReference;
import com.example.clinical.domain.entity.SpeechSample;
import com.example.clinical.dto.SpeechSampleCreateRequest;
import com.example.clinical.dto.SpeechSampleResponse;
import com.example.clinical.repository.AssessmentTaskAssignmentRepository;
import com.example.clinical.repository.LanguageReferenceRepository;
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
public class SpeechSampleService {

    private final SpeechSampleRepository speechSampleRepository;
    private final AssessmentTaskAssignmentRepository assignmentRepository;
    private final LanguageReferenceRepository languageReferenceRepository;

    @Transactional
    public SpeechSampleResponse createSpeechSample(UUID assignmentId, SpeechSampleCreateRequest request) {
        AssessmentTaskAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

        LanguageReference language = languageReferenceRepository.findById(request.getAssessmentLanguageCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Language not found"));

        SpeechSample sample = new SpeechSample();
        sample.setAssignment(assignment);
        sample.setAssessmentLanguage(language);
        sample.setSpeakerType(request.getSpeakerType());
        sample.setSpeakerCount(request.getSpeakerCount());
        sample.setSampleDurationSeconds(request.getSampleDurationSeconds());
        sample.setSampleAudioUrl(request.getSampleAudioUrl());
        sample.setRecordingQuality(request.getRecordingQuality());
        sample.setBackgroundNoiseLevel(request.getBackgroundNoiseLevel());
        sample.setAnalysisEligibility(request.getAnalysisEligibility());
        sample.setAnalysisIneligibilityReason(request.getAnalysisIneligibilityReason());

        SpeechSample savedSample = speechSampleRepository.save(sample);
        return mapToResponse(savedSample);
    }

    public List<SpeechSampleResponse> getSpeechSamplesByAssignment(UUID assignmentId) {
        return speechSampleRepository.findByAssignmentId(assignmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SpeechSampleResponse mapToResponse(SpeechSample sample) {
        SpeechSampleResponse response = new SpeechSampleResponse();
        response.setId(sample.getId());
        response.setAssignmentId(sample.getAssignment().getId());
        response.setAssessmentLanguageCode(sample.getAssessmentLanguage().getLanguageCode());
        response.setSpeakerType(sample.getSpeakerType());
        response.setSpeakerCount(sample.getSpeakerCount());
        response.setSampleDurationSeconds(sample.getSampleDurationSeconds());
        response.setSampleAudioUrl(sample.getSampleAudioUrl());
        response.setRecordingQuality(sample.getRecordingQuality());
        response.setBackgroundNoiseLevel(sample.getBackgroundNoiseLevel());
        response.setAnalysisEligibility(sample.getAnalysisEligibility());
        response.setAnalysisIneligibilityReason(sample.getAnalysisIneligibilityReason());
        return response;
    }
}
