package com.example.clinical.dto;

import com.example.clinical.domain.enums.AnalysisEligibility;
import com.example.clinical.domain.enums.AnalysisIneligibilityReason;
import com.example.clinical.domain.enums.NoiseLevel;
import com.example.clinical.domain.enums.RecordingQuality;
import com.example.clinical.domain.enums.SpeakerType;
import lombok.Data;

@Data
public class SpeechSampleCreateRequest {
    private String assessmentLanguageCode;
    private SpeakerType speakerType;
    private Integer speakerCount;
    private Integer sampleDurationSeconds;
    private String sampleAudioUrl;
    private RecordingQuality recordingQuality;
    private NoiseLevel backgroundNoiseLevel;
    private AnalysisEligibility analysisEligibility;
    private AnalysisIneligibilityReason analysisIneligibilityReason;
}
