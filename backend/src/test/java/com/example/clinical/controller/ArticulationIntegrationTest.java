package com.example.clinical.controller;

import com.example.clinical.DemoApplication;
import com.example.clinical.domain.entity.*;
import com.example.clinical.domain.enums.*;
import com.example.clinical.repository.*;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ArticulationIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private ClinicalCaseRepository caseRepository;
    @Autowired private EncounterRepository encounterRepository;
    @Autowired private ArticulationTargetLibraryRepository targetRepository;
    @Autowired private LanguageReferenceRepository languageRepository;

    private User therapist;
    private User supervisor;
    private Encounter encounter;
    private ArticulationTargetLibrary target;

    @BeforeEach
    void setUp() {
        therapist = new User();
        therapist.setName("Dr. Therapist");
        therapist.setRole(UserRole.THERAPIST);
        therapist.setStatus(UserStatus.ACTIVE);
        therapist = userRepository.saveAndFlush(therapist);

        supervisor = new User();
        supervisor.setName("Dr. Supervisor");
        supervisor.setRole(UserRole.SUPERVISOR);
        supervisor.setStatus(UserStatus.ACTIVE);
        supervisor = userRepository.saveAndFlush(supervisor);

        Patient patient = new Patient();
        patient.setLegalName("Test Patient");
        patient.setDateOfBirth(LocalDate.of(2015, 5, 10));
        patient.setContactNumber("555-1234");
        patient = patientRepository.saveAndFlush(patient);

        ClinicalCase clinicalCase = new ClinicalCase();
        clinicalCase.setPatient(patient);
        clinicalCase.setCaseStatus(CaseStatus.ACTIVE);
        clinicalCase.setAssignedTherapist(therapist);
        clinicalCase.setAssignedSupervisor(supervisor);
        clinicalCase = caseRepository.saveAndFlush(clinicalCase);

        encounter = new Encounter();
        encounter.setClinicalCase(clinicalCase);
        encounter.setEncounterType(EncounterType.INITIAL_EVALUATION);
        encounter.setEncounterDateTime(ZonedDateTime.now());
        encounter.setAssessmentReason(AssessmentReason.INITIAL_ASSESSMENT);
        encounter = encounterRepository.saveAndFlush(encounter);

        LanguageReference lang = new LanguageReference();
        lang.setLanguageCode("EN");
        lang.setLanguageName("English");
        lang = languageRepository.saveAndFlush(lang);

        target = new ArticulationTargetLibrary();
        target.setTargetVersion(1);
        target.setLanguage(lang);
        target.setTargetType(TargetType.WORD);
        target.setPhoneme("s");
        target.setWord("sun");
        target.setPosition(TargetPosition.INITIAL);
        target.setCreatedAt(ZonedDateTime.now());
        target = targetRepository.saveAndFlush(target);
    }

    @Test
    void shouldCreateContextAndAttempt() throws Exception {
        // 1. Create Context
        Map<String, Object> contextReq = new HashMap<>();
        contextReq.put("contextDescription", "Picture naming task");

        MvcResult contextResult = mockMvc.perform(post("/api/v1/encounters/" + encounter.getId() + "/articulation/contexts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contextReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.contextDescription").value("Picture naming task"))
                .andReturn();

        String contextId = objectMapper.readTree(contextResult.getResponse().getContentAsString()).get("id").asText();

        // 2. Create Attempt (Substitution error in initial position)
        Map<String, Object> attemptReq = new HashMap<>();
        attemptReq.put("targetId", target.getId().toString());
        attemptReq.put("productionContextId", contextId);
        attemptReq.put("attemptNumber", 1);
        attemptReq.put("productionAccuracy", ArticulationAccuracy.SUBSTITUTION.name());
        attemptReq.put("producedPhoneme", "th");
        attemptReq.put("clinicianObservation", "Frontal lisp");
        attemptReq.put("productionAudioUrl", "http://example.com/audio.mp3");

        MvcResult attemptResult = mockMvc.perform(post("/api/v1/encounters/" + encounter.getId() + "/articulation/attempts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(attemptReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.productionAccuracy").value("SUBSTITUTION"))
                .andExpect(jsonPath("$.producedPhoneme").value("th"))
                .andReturn();

        String attemptId = objectMapper.readTree(attemptResult.getResponse().getContentAsString()).get("id").asText();

        // 3. Retrieve Attempts
        mockMvc.perform(get("/api/v1/encounters/" + encounter.getId() + "/articulation/attempts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(attemptId))
                .andExpect(jsonPath("$[0].productionAccuracy").value("SUBSTITUTION"));
    }
}
