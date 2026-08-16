package com.example.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenceSummaryResponse {
    private EncounterResponse encounter;
    private List<HearingScreeningResponse> hearingScreenings;
    private List<SpeechSampleResponse> speechSamples;
    private List<ArticulationProductionResponse> articulationResults;
    private List<FluencyAssessmentResponse> fluencyResults;
}
