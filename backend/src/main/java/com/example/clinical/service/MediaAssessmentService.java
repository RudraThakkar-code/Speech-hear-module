package com.example.clinical.service;

import com.example.clinical.domain.entity.*;
import com.example.clinical.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaAssessmentService {
    private final VoiceAssessmentRepository voiceAssessmentRepository;
    private final VoiceObjectiveMeasurementRepository measurementRepository;
    private final VideoObservationRepository videoObservationRepository;
    private final AiArtifactRepository aiArtifactRepository;
    private final EncounterRepository encounterRepository;
    private final SpeechSampleRepository speechSampleRepository;
    private final ClinicalInterpretationRepository interpretationRepository;
    private final UserRepository userRepository;

    @Transactional
    public VoiceAssessment createVoice(UUID encounterId, UUID sampleId, Map<String, Object> body) {
        VoiceAssessment v = new VoiceAssessment();
        v.setEncounter(encounterRepository.findById(encounterId).orElseThrow(() -> new IllegalArgumentException("Encounter not found")));
        v.setVoiceSample(speechSampleRepository.findById(sampleId).orElseThrow(() -> new IllegalArgumentException("Speech sample not found")));
        v.setPitchObservation(String.valueOf(body.get("pitchObservation")));
        v.setLoudnessObservation(String.valueOf(body.get("loudnessObservation")));
        Object q = body.get("voiceQuality");
        v.setVoiceQuality(q instanceof java.util.Collection<?> c ? c.stream().map(String::valueOf).toArray(String[]::new) : new String[] { String.valueOf(q) });
        v.setResonanceObservation(String.valueOf(body.get("resonanceObservation")));
        v.setVoiceConcernFlag(Boolean.parseBoolean(String.valueOf(body.getOrDefault("voiceConcernFlag", false))));
        return voiceAssessmentRepository.save(v);
    }

    @Transactional
    public VoiceObjectiveMeasurement addMeasurement(UUID sampleId, Map<String, Object> body) {
        VoiceObjectiveMeasurement m = new VoiceObjectiveMeasurement();
        m.setVoiceSample(speechSampleRepository.findById(sampleId).orElseThrow(() -> new IllegalArgumentException("Speech sample not found")));
        m.setParameterName(String.valueOf(body.get("parameterName")));
        m.setValue(new BigDecimal(String.valueOf(body.get("value"))));
        m.setUnit(String.valueOf(body.get("unit")));
        m.setMeasurementSource(String.valueOf(body.get("measurementSource")));
        m.setMeasurementMethod(String.valueOf(body.get("measurementMethod")));
        return measurementRepository.save(m);
    }

    @Transactional
    public VideoObservation createVideo(UUID encounterId, Map<String, Object> body) {
        VideoObservation v = new VideoObservation();
        v.setEncounter(encounterRepository.findById(encounterId).orElseThrow(() -> new IllegalArgumentException("Encounter not found")));
        v.setVideoSampleUrl(String.valueOf(body.get("videoSampleUrl")));
        v.setCameraPosition(String.valueOf(body.get("cameraPosition")));
        v.setVideoContext(String.valueOf(body.get("videoContext")));
        v.setSecondaryBehaviorEye(body.get("secondaryBehaviorEye") == null ? null : String.valueOf(body.get("secondaryBehaviorEye")));
        v.setSecondaryBehaviorFacial(body.get("secondaryBehaviorFacial") == null ? null : String.valueOf(body.get("secondaryBehaviorFacial")));
        v.setSecondaryBehaviorLimb(body.get("secondaryBehaviorLimb") == null ? null : String.valueOf(body.get("secondaryBehaviorLimb")));
        return videoObservationRepository.save(v);
    }

    @Transactional
    public AiArtifact createAiArtifact(Map<String, Object> body) {
        AiArtifact a = new AiArtifact();
        if (body.get("speechSampleId") != null) a.setSpeechSample(speechSampleRepository.findById(UUID.fromString(String.valueOf(body.get("speechSampleId")))).orElseThrow(() -> new IllegalArgumentException("Speech sample not found")));
        if (body.get("videoObservationId") != null) a.setVideoObservation(videoObservationRepository.findById(UUID.fromString(String.valueOf(body.get("videoObservationId")))).orElseThrow(() -> new IllegalArgumentException("Video observation not found")));
        if (body.get("clinicalInterpretationId") != null) a.setClinicalInterpretation(interpretationRepository.findById(UUID.fromString(String.valueOf(body.get("clinicalInterpretationId")))).orElseThrow(() -> new IllegalArgumentException("Clinical Interpretation not found")));
        a.setModelVersion(String.valueOf(body.get("modelVersion")));
        a.setAnalysisType(String.valueOf(body.get("analysisType")));
        a.setAiGeneratedResult(body.getOrDefault("aiGeneratedResult", Map.of()));
        a.setConfidenceScore(new BigDecimal(String.valueOf(body.getOrDefault("confidenceScore", "0"))));
        a.setHumanReviewStatus(String.valueOf(body.getOrDefault("humanReviewStatus", "PENDING")));
        if (body.get("reviewedBy") != null) {
            a.setReviewedBy(userRepository.findById(UUID.fromString(String.valueOf(body.get("reviewedBy")))).orElseThrow(() -> new IllegalArgumentException("Reviewer not found")));
            a.setReviewedAt(ZonedDateTime.now());
        }
        return aiArtifactRepository.save(a);
    }
}
