package com.example.clinical.dto;
import com.example.clinical.domain.enums.*;
import jakarta.validation.constraints.Min; import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
public record SpeechSampleCreateRequest(@NotNull java.util.UUID assignmentId,@NotBlank String assessmentLanguageCode,@NotNull SpeakerType speakerType,Integer speakerCount,@NotNull @Min(1) Integer sampleDurationSeconds,@NotBlank String sampleAudioUrl,@NotNull RecordingQuality recordingQuality,@NotNull NoiseLevel backgroundNoiseLevel,@NotNull AnalysisEligibility analysisEligibility,AnalysisIneligibilityReason analysisIneligibilityReason) {}
