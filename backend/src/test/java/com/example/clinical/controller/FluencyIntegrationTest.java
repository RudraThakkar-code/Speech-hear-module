package com.example.clinical.controller;

import com.example.clinical.DemoApplication;
import com.example.clinical.domain.entity.AssessmentTaskAssignment;
import com.example.clinical.domain.entity.AssessmentTaskLibrary;
import com.example.clinical.domain.entity.ClinicalCase;
import com.example.clinical.domain.entity.Encounter;
import com.example.clinical.domain.entity.FluencyAssessment;
import com.example.clinical.domain.entity.LanguageReference;
import com.example.clinical.domain.entity.Patient;
import com.example.clinical.domain.entity.SpeechSample;
import com.example.clinical.domain.entity.User;
import com.example.clinical.domain.enums.AnalysisEligibility;
import com.example.clinical.domain.enums.AssessmentReason;
import com.example.clinical.domain.enums.AssignmentStatus;
import com.example.clinical.domain.enums.CaseStatus;
import com.example.clinical.domain.enums.EncounterType;
import com.example.clinical.domain.enums.NoiseLevel;
import com.example.clinical.domain.enums.RecordingQuality;
import com.example.clinical.domain.enums.SpeakerType;
import com.example.clinical.domain.enums.SpeechRateObservation;
import com.example.clinical.domain.enums.SpeechRateUnit;
import com.example.clinical.domain.enums.TaskType;
import com.example.clinical.domain.enums.UserRole;
import com.example.clinical.domain.enums.UserStatus;
import com.example.clinical.repository.AssessmentTaskAssignmentRepository;
import com.example.clinical.repository.AssessmentTaskLibraryRepository;
import com.example.clinical.repository.ClinicalCaseRepository;
import com.example.clinical.repository.EncounterRepository;
import com.example.clinical.repository.FluencyAssessmentRepository;
import com.example.clinical.repository.LanguageReferenceRepository;
import com.example.clinical.repository.PatientRepository;
import com.example.clinical.repository.SpeechSampleRepository;
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
class FluencyIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private ClinicalCaseRepository caseRepository;
    @Autowired private EncounterRepository encounterRepository;
    @Autowired private AssessmentTaskLibraryRepository taskRepository;
    @Autowired private AssessmentTaskAssignmentRepository assignmentRepository;
    @Autowired private SpeechSampleRepository speechSampleRepository;
    @Autowired private LanguageReferenceRepository languageRepository;

    private User therapist;
    private User supervisor;
    private Encounter encounter;
    private SpeechSample speechSample;

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

        AssessmentTaskLibrary task = new AssessmentTaskLibrary();
        task.setTaskVersion(1);
        task.setLanguage(lang);
        task.setTaskType(TaskType.SPONTANEOUS);
        task.setInstructions("Talk for 1 minute.");
        task.setPromptMaterial("None");
        task.setCreatedAt(ZonedDateTime.now());
        task = taskRepository.saveAndFlush(task);

        AssessmentTaskAssignment assignment = new AssessmentTaskAssignment();
        assignment.setTask(task);
        assignment.setEncounter(encounter);
        assignment.setAssignedBy(therapist);
        assignment.setSequenceOrder(1);
        assignment.setRequired(true);
        assignment.setCompletionStatus(AssignmentStatus.COMPLETED);
        assignment = assignmentRepository.saveAndFlush(assignment);

        speechSample = new SpeechSample();
        speechSample.setAssignment(assignment);
        speechSample.setAssessmentLanguage(lang);
        speechSample.setSpeakerType(SpeakerType.PATIENT);
        speechSample.setSpeakerCount(1);
        speechSample.setSampleDurationSeconds(120);
        speechSample.setSampleAudioUrl("http://example.com/fluency.mp3");
        speechSample.setRecordingQuality(RecordingQuality.GOOD);
        speechSample.setBackgroundNoiseLevel(NoiseLevel.LOW);
        speechSample.setAnalysisEligibility(AnalysisEligibility.ELIGIBLE);
        speechSample = speechSampleRepository.saveAndFlush(speechSample);
    }

    @Test
    void shouldCreateAndRetrieveFluencyAssessment() throws Exception {
        // 1. Create Fluency Assessment
        Map<String, Object> req = new HashMap<>();
        req.put("fluencySampleId", speechSample.getId().toString());
        req.put("speechRateObservation", SpeechRateObservation.FAST.name());
        req.put("speechRateValue", 200.5);
        req.put("speechRateUnit", SpeechRateUnit.SYLLABLES_PER_MIN.name());
        req.put("totalSyllables", 401);
        req.put("totalWords", 250);
        req.put("repetitionCount", 15);
        req.put("prolongationDurationEst", 5.5);
        req.put("blockDurationEst", 2.0);
        req.put("atypicalPauseCount", 3);

        MvcResult createResult = mockMvc.perform(post("/api/v1/encounters/" + encounter.getId() + "/fluency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.speechRateObservation").value("FAST"))
                .andExpect(jsonPath("$.speechRateValue").value(200.5))
                .andExpect(jsonPath("$.totalSyllables").value(401))
                .andExpect(jsonPath("$.prolongationDurationEst").value(5.5))
                .andReturn();

        String assessmentId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // 2. Retrieve Fluency Assessments
        mockMvc.perform(get("/api/v1/encounters/" + encounter.getId() + "/fluency"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(assessmentId))
                .andExpect(jsonPath("$[0].repetitionCount").value(15))
                .andExpect(jsonPath("$[0].blockDurationEst").value(2.0))
                .andExpect(jsonPath("$[0].atypicalPauseCount").value(3));
    }

    @Test
    void shouldReturn404ForUnknownEncounter() throws Exception {
        Map<String, Object> req = new HashMap<>();
        req.put("fluencySampleId", speechSample.getId().toString());
        req.put("speechRateObservation", SpeechRateObservation.TYPICAL.name());

        mockMvc.perform(post("/api/v1/encounters/" + UUID.randomUUID() + "/fluency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400ForInvalidSampleId() throws Exception {
        Map<String, Object> req = new HashMap<>();
        req.put("fluencySampleId", "invalid-uuid");
        req.put("speechRateObservation", SpeechRateObservation.TYPICAL.name());

        mockMvc.perform(post("/api/v1/encounters/" + encounter.getId() + "/fluency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}
