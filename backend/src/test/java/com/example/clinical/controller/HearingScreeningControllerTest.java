package com.example.clinical.controller;

import com.example.clinical.DemoApplication;
import com.example.clinical.domain.entity.ClinicalCase;
import com.example.clinical.domain.entity.Encounter;
import com.example.clinical.domain.entity.Patient;
import com.example.clinical.domain.entity.User;
import com.example.clinical.domain.enums.*;
import com.example.clinical.repository.ClinicalCaseRepository;
import com.example.clinical.repository.EncounterRepository;
import com.example.clinical.repository.PatientRepository;
import com.example.clinical.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class HearingScreeningControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserRepository userRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private ClinicalCaseRepository clinicalCaseRepository;
    @Autowired private EncounterRepository encounterRepository;

    private Encounter encounter;

    @BeforeEach
    void setUp() {
        User therapist = new User();
        therapist.setName("Dr. Therapist");
        therapist.setRole(UserRole.THERAPIST);
        therapist.setStatus(UserStatus.ACTIVE);
        therapist = userRepository.saveAndFlush(therapist);

        Patient patient = new Patient();
        patient.setLegalName("Jane Doe");
        patient.setDateOfBirth(LocalDate.of(2010, 1, 1));
        patient.setContactNumber("123-456");
        patient = patientRepository.saveAndFlush(patient);

        ClinicalCase clinicalCase = new ClinicalCase();
        clinicalCase.setPatient(patient);
        clinicalCase.setCaseStatus(CaseStatus.ACTIVE);
        clinicalCase.setAssignedTherapist(therapist);
        clinicalCase.setAssignedSupervisor(therapist);
        clinicalCase = clinicalCaseRepository.saveAndFlush(clinicalCase);

        encounter = new Encounter();
        encounter.setClinicalCase(clinicalCase);
        encounter.setEncounterType(EncounterType.INITIAL_EVALUATION);
        encounter.setEncounterDateTime(ZonedDateTime.now());
        encounter.setAssessmentReason(AssessmentReason.SCREENING);
        encounter = encounterRepository.saveAndFlush(encounter);
    }

    @Test
    void shouldCreateScreeningAndMeasurements() throws Exception {
        // 1. Create Hearing Screening
        Map<String, Object> screeningReq = new HashMap<>();
        screeningReq.put("screeningDate", "2026-08-15");
        screeningReq.put("screeningEnvironment", ScreeningEnvironment.SOUND_TREATED_ROOM.name());
        screeningReq.put("screeningDeviceUsed", "Standard Audiometer");
        screeningReq.put("screeningOutcome", ScreeningOutcome.NO_CONCERN.name());
        screeningReq.put("recommendedAction", HearingRecommendedAction.NO_IMMEDIATE_ACTION.name());

        MvcResult screeningResult = mockMvc.perform(post("/api/v1/encounters/" + encounter.getId() + "/hearing-screenings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(screeningReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.screeningOutcome").value("NO_CONCERN"))
                .andReturn();

        String screeningId = objectMapper.readTree(screeningResult.getResponse().getContentAsString()).get("id").asText();

        // 2. Add Measurement
        Map<String, Object> measurementReq = new HashMap<>();
        measurementReq.put("trialOrder", 1);
        measurementReq.put("ear", EarType.LEFT.name());
        measurementReq.put("frequencyPresented", 1000);
        measurementReq.put("intensityPresented", 20);
        measurementReq.put("responseMethod", ResponseMethod.BUTTON_PRESS.name());
        measurementReq.put("patientResponse", PatientResponse.CONSISTENT.name());

        mockMvc.perform(post("/api/v1/hearing-screenings/" + screeningId + "/measurements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(measurementReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.frequencyPresented").value(1000));

        // 3. Retrieve Hearing Screening
        mockMvc.perform(get("/api/v1/hearing-screenings/" + screeningId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.screeningEnvironment").value("SOUND_TREATED_ROOM"));
    }

    @Test
    void shouldReturn404ForUnknownEncounter() throws Exception {
        Map<String, Object> screeningReq = new HashMap<>();
        screeningReq.put("screeningDate", "2026-08-15");
        screeningReq.put("screeningEnvironment", ScreeningEnvironment.SOUND_TREATED_ROOM.name());
        screeningReq.put("screeningDeviceUsed", "Standard Audiometer");
        screeningReq.put("screeningOutcome", ScreeningOutcome.NO_CONCERN.name());
        screeningReq.put("recommendedAction", HearingRecommendedAction.NO_IMMEDIATE_ACTION.name());

        mockMvc.perform(post("/api/v1/encounters/" + UUID.randomUUID() + "/hearing-screenings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(screeningReq)))
                .andExpect(status().isNotFound());
    }
}
