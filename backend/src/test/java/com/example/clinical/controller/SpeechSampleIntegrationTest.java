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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SpeechSampleIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private ClinicalCaseRepository caseRepository;
    @Autowired private EncounterRepository encounterRepository;
    @Autowired private AssessmentTaskLibraryRepository taskRepository;
    @Autowired private LanguageReferenceRepository languageRepository;

    private User therapist;
    private User supervisor;
    private Encounter encounter;
    private AssessmentTaskLibrary task;

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

        task = new AssessmentTaskLibrary();
        task.setTaskVersion(1);
        task.setLanguage(lang);
        task.setTaskType(TaskType.SPONTANEOUS);
        task.setInstructions("Talk for 1 minute.");
        task.setPromptMaterial("None");
        task.setCreatedAt(ZonedDateTime.now());
        task = taskRepository.saveAndFlush(task);
    }

    @Test
    void shouldCreateAssignmentAndSpeechSample() throws Exception {
        // 1. Create Assignment
        Map<String, Object> assignmentReq = new HashMap<>();
        assignmentReq.put("taskId", task.getId().toString());
        assignmentReq.put("assignedById", therapist.getId().toString());
        assignmentReq.put("sequenceOrder", 1);
        assignmentReq.put("required", true);
        assignmentReq.put("completionStatus", AssignmentStatus.COMPLETED.name());

        MvcResult assignmentResult = mockMvc.perform(post("/api/v1/encounters/" + encounter.getId() + "/assignments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignmentReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.sequenceOrder").value(1))
                .andReturn();

        String assignmentId = objectMapper.readTree(assignmentResult.getResponse().getContentAsString()).get("id").asText();

        // 2. Create Speech Sample
        Map<String, Object> sampleReq = new HashMap<>();
        sampleReq.put("assessmentLanguageCode", "EN");
        sampleReq.put("speakerType", SpeakerType.PATIENT.name());
        sampleReq.put("speakerCount", 1);
        sampleReq.put("sampleDurationSeconds", 60);
        sampleReq.put("sampleAudioUrl", "http://example.com/audio.mp3");
        sampleReq.put("recordingQuality", RecordingQuality.GOOD.name());
        sampleReq.put("backgroundNoiseLevel", NoiseLevel.LOW.name());
        sampleReq.put("analysisEligibility", AnalysisEligibility.ELIGIBLE.name());

        MvcResult sampleResult = mockMvc.perform(post("/api/v1/assignments/" + assignmentId + "/samples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.sampleAudioUrl").value("http://example.com/audio.mp3"))
                .andReturn();

        String sampleId = objectMapper.readTree(sampleResult.getResponse().getContentAsString()).get("id").asText();

        // 3. Retrieve Speech Samples
        mockMvc.perform(get("/api/v1/assignments/" + assignmentId + "/samples"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sampleId));
    }
}
