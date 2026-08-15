package com.example.clinical.controller;

import com.example.clinical.DemoApplication;
import com.example.clinical.domain.entity.User;
import com.example.clinical.domain.enums.AssessmentReason;
import com.example.clinical.domain.enums.EncounterType;
import com.example.clinical.domain.enums.UserRole;
import com.example.clinical.domain.enums.UserStatus;
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
class ClinicalWorkflowControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;

    private User therapist;
    private User supervisor;

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
    }

    @Test
    void shouldExecuteCoreApiWorkflow() throws Exception {
        // 1. Create Patient
        Map<String, Object> patientReq = new HashMap<>();
        patientReq.put("legalName", "API Patient");
        patientReq.put("dateOfBirth", "2015-05-10");
        patientReq.put("contactNumber", "555-1234");
        patientReq.put("sexAtBirth", "MALE");

        MvcResult patientResult = mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.legalName").value("API Patient"))
                .andReturn();

        String patientIdStr = objectMapper.readTree(patientResult.getResponse().getContentAsString()).get("id").asText();

        // 2. Retrieve Patient (Verify Validation)
        mockMvc.perform(get("/api/v1/patients/" + patientIdStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legalName").value("API Patient"));

        // 3. Create Case
        Map<String, Object> caseReq = new HashMap<>();
        caseReq.put("patientId", patientIdStr);
        caseReq.put("assignedTherapistId", therapist.getId().toString());
        caseReq.put("assignedSupervisorId", supervisor.getId().toString());

        MvcResult caseResult = mockMvc.perform(post("/api/v1/cases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(caseReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.caseStatus").value("ACTIVE"))
                .andReturn();

        String caseIdStr = objectMapper.readTree(caseResult.getResponse().getContentAsString()).get("id").asText();

        // 4. Create Encounter
        Map<String, Object> encounterReq = new HashMap<>();
        encounterReq.put("encounterType", EncounterType.INITIAL_EVALUATION.name());
        encounterReq.put("encounterDateTime", "2026-08-15T12:00:00Z");
        encounterReq.put("assessmentReason", AssessmentReason.CHIEF_COMPLAINT.name());

        MvcResult encounterResult = mockMvc.perform(post("/api/v1/cases/" + caseIdStr + "/encounters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(encounterReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.encounterType").value("INITIAL_EVALUATION"))
                .andReturn();

        String encounterIdStr = objectMapper.readTree(encounterResult.getResponse().getContentAsString()).get("id").asText();

        // 5. Retrieve Encounter
        mockMvc.perform(get("/api/v1/encounters/" + encounterIdStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseId").value(caseIdStr));
    }

    @Test
    void shouldReturn404ForUnknownPatient() throws Exception {
        mockMvc.perform(get("/api/v1/patients/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldReturn422ForInvalidPatient() throws Exception {
        Map<String, Object> invalidPatientReq = new HashMap<>();
        invalidPatientReq.put("legalName", ""); // Blank name should fail validation

        mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPatientReq)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.legalName").exists());
    }
}
