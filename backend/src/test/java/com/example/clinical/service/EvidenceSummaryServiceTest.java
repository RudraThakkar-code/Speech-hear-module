package com.example.clinical.service;

import com.example.clinical.domain.entity.Encounter;
import com.example.clinical.dto.EncounterResponse;
import com.example.clinical.dto.EvidenceSummaryResponse;
import com.example.clinical.repository.ArticulationProductionAttemptRepository;
import com.example.clinical.repository.EncounterRepository;
import com.example.clinical.repository.FluencyAssessmentRepository;
import com.example.clinical.repository.HearingScreeningRepository;
import com.example.clinical.repository.SpeechSampleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EvidenceSummaryServiceTest {

    @Mock
    private EncounterRepository encounterRepository;

    @Mock
    private EncounterService encounterService;

    @Mock
    private HearingScreeningRepository hearingScreeningRepository;

    @Mock
    private SpeechSampleRepository speechSampleRepository;

    @Mock
    private ArticulationProductionAttemptRepository articulationRepository;

    @Mock
    private FluencyAssessmentRepository fluencyRepository;

    @InjectMocks
    private EvidenceSummaryService evidenceSummaryService;

    @Test
    void shouldGetEvidenceSummarySuccessfully() {
        UUID encounterId = UUID.randomUUID();
        Encounter encounter = new Encounter();
        encounter.setId(encounterId);

        EncounterResponse encounterResponse = new EncounterResponse();
        encounterResponse.setId(encounterId);

        when(encounterRepository.findById(encounterId)).thenReturn(Optional.of(encounter));
        when(encounterService.getEncounter(encounterId)).thenReturn(encounterResponse);
        when(hearingScreeningRepository.findByEncounter(any(Encounter.class))).thenReturn(List.of());
        when(speechSampleRepository.findByEncounter(any(Encounter.class))).thenReturn(List.of());
        when(articulationRepository.findByEncounter(any(Encounter.class))).thenReturn(List.of());
        when(fluencyRepository.findByEncounter(any(Encounter.class))).thenReturn(List.of());

        EvidenceSummaryResponse response = evidenceSummaryService.getEvidenceSummary(encounterId);

        assertNotNull(response);
        assertEquals(encounterId, response.getEncounter().getId());
        assertEquals(0, response.getHearingScreenings().size());
        assertEquals(0, response.getSpeechSamples().size());
        assertEquals(0, response.getArticulationResults().size());
        assertEquals(0, response.getFluencyResults().size());
    }

    @Test
    void shouldThrowExceptionWhenEncounterNotFound() {
        UUID encounterId = UUID.randomUUID();
        when(encounterRepository.findById(encounterId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> evidenceSummaryService.getEvidenceSummary(encounterId));
    }
}
