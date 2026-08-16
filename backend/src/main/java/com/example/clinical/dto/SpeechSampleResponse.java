package com.example.clinical.dto;
import com.example.clinical.domain.enums.*; import java.util.UUID;
public record SpeechSampleResponse(UUID id, UUID assignmentId, String assessmentLanguageCode, SpeakerType speakerType, Integer speakerCount, Integer sampleDurationSeconds, String sampleAudioUrl, RecordingQuality recordingQuality, NoiseLevel backgroundNoiseLevel, AnalysisEligibility analysisEligibility, AnalysisIneligibilityReason analysisIneligibilityReason) {}
