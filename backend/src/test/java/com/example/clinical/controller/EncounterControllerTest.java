package com.example.clinical.controller;

import com.example.clinical.domain.enums.AssessmentReason;
import com.example.clinical.domain.enums.EncounterType;
import com.example.clinical.dto.EncounterResponse;
import com.example.clinical.service.EncounterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EncounterController.class)
class EncounterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EncounterService encounterService;

    @Test
    void shouldReturnEncounterWhenFound() throws Exception {
        UUID encounterId = UUID.randomUUID();
        UUID caseId = UUID.randomUUID();
        ZonedDateTime dateTime = ZonedDateTime.parse("2026-08-15T12:00:00Z");

        EncounterResponse mockResponse = new EncounterResponse();
        mockResponse.setId(encounterId);
        mockResponse.setCaseId(caseId);
        mockResponse.setEncounterType(EncounterType.INITIAL_EVALUATION);
        mockResponse.setEncounterDateTime(dateTime);
        mockResponse.setAssessmentReason(AssessmentReason.CHIEF_COMPLAINT);

        when(encounterService.getEncounter(encounterId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/encounters/{encounterId}", encounterId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(encounterId.toString()))
                .andExpect(jsonPath("$.caseId").value(caseId.toString()))
                .andExpect(jsonPath("$.encounterType").value("INITIAL_EVALUATION"))
                .andExpect(jsonPath("$.encounterDateTime").value(DateTimeFormatter.ISO_INSTANT.format(dateTime)))
                .andExpect(jsonPath("$.assessmentReason").value("CHIEF_COMPLAINT"));
    }

    @Test
    void shouldReturn404WhenEncounterNotFound() throws Exception {
        UUID encounterId = UUID.randomUUID();

        when(encounterService.getEncounter(encounterId))
                .thenThrow(new IllegalArgumentException("Encounter not found"));

        mockMvc.perform(get("/api/v1/encounters/{encounterId}", encounterId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Encounter not found"));
    }

    @Test
    void shouldRejectInvalidEncounterId() throws Exception {
        String invalidId = "not-a-uuid";

        mockMvc.perform(get("/api/v1/encounters/{encounterId}", invalidId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Or whatever status is mapped for method arg type mismatch
    }
}
