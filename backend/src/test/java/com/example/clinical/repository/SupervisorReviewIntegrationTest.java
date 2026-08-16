package com.example.clinical.repository;

import com.example.clinical.domain.entity.ClinicalCase;
import com.example.clinical.domain.entity.ClinicalInterpretation;
import com.example.clinical.domain.entity.Encounter;
import com.example.clinical.domain.entity.Patient;
import com.example.clinical.domain.entity.User;
import com.example.clinical.domain.enums.AssessmentReason;
import com.example.clinical.domain.enums.CaseStatus;
import com.example.clinical.domain.enums.ClinicalAssessmentStatus;
import com.example.clinical.domain.enums.ClinicalRecommendedAction;
import com.example.clinical.domain.enums.EncounterType;
import com.example.clinical.domain.enums.SupervisorAction;
import com.example.clinical.domain.enums.UserRole;
import com.example.clinical.domain.enums.UserStatus;
import com.example.clinical.dto.SupervisorReviewRequest;
import com.example.clinical.dto.SupervisorReviewResponse;
import com.example.clinical.service.SupervisorReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class SupervisorReviewIntegrationTest {

    @Autowired
    private SupervisorReviewService supervisorReviewService;

    @Autowired
    private ClinicalInterpretationRepository interpretationRepository;

    @Autowired
    private EncounterRepository encounterRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ClinicalCaseRepository clinicalCaseRepository;

    @Autowired
    private UserRepository userRepository;

    private User supervisor;
    private ClinicalInterpretation interpretation;

    @BeforeEach
    void setup() {
        Patient patient = new Patient();
        patient.setLegalName("Review Patient");
        patient.setDateOfBirth(LocalDate.of(2010, 1, 1));
        patient = patientRepository.save(patient);

        ClinicalCase clinicalCase = new ClinicalCase();
        clinicalCase.setPatient(patient);
        clinicalCase.setCaseStatus(CaseStatus.ACTIVE);
        clinicalCase = clinicalCaseRepository.save(clinicalCase);

        Encounter encounter = new Encounter();
        encounter.setClinicalCase(clinicalCase);
        encounter.setEncounterType(EncounterType.INITIAL_EVALUATION);
        encounter.setEncounterDateTime(java.time.ZonedDateTime.now());
        encounter.setAssessmentReason(AssessmentReason.INITIAL_ASSESSMENT);
        encounter = encounterRepository.save(encounter);

        supervisor = new User();
        supervisor.setRole(UserRole.SUPERVISOR);
        supervisor.setStatus(UserStatus.ACTIVE);
        supervisor.setEmail("supervisor@example.com");
        supervisor.setName("Test Supervisor");
        supervisor = userRepository.save(supervisor);

        interpretation = new ClinicalInterpretation();
        interpretation.setEncounter(encounter);
        interpretation.setEvidenceSummary("Summary before review");
        interpretation.setClinicalAssessmentStatus(ClinicalAssessmentStatus.SUBMITTED);
        interpretation.setRecommendedAction(ClinicalRecommendedAction.THERAPY);
        interpretation = interpretationRepository.save(interpretation);
    }

    @Test
    void shouldSuccessfullyApproveAndFinalizeInterpretation() {
        SupervisorReviewRequest request = new SupervisorReviewRequest();
        request.setSupervisorId(supervisor.getId());
        request.setAction(SupervisorAction.APPROVE);
        request.setComments("Excellent assessment.");

        SupervisorReviewResponse response = supervisorReviewService.submitReview(interpretation.getClinicalInterpretationId(), request);

        assertNotNull(response);
        assertEquals(SupervisorAction.APPROVE, response.getAction());

        ClinicalInterpretation updated = interpretationRepository.findById(interpretation.getClinicalInterpretationId()).orElseThrow();
        assertEquals(ClinicalAssessmentStatus.FINALIZED, updated.getClinicalAssessmentStatus());
        assertEquals("Summary before review", updated.getEvidenceSummary());
    }

    @Test
    void shouldSuccessfullyReturnForCorrection() {
        SupervisorReviewRequest request = new SupervisorReviewRequest();
        request.setSupervisorId(supervisor.getId());
        request.setAction(SupervisorAction.RETURN_FOR_CORRECTION);
        request.setComments("Please add more details to the articulation problems.");

        SupervisorReviewResponse response = supervisorReviewService.submitReview(interpretation.getClinicalInterpretationId(), request);

        assertNotNull(response);
        assertEquals(SupervisorAction.RETURN_FOR_CORRECTION, response.getAction());

        ClinicalInterpretation updated = interpretationRepository.findById(interpretation.getClinicalInterpretationId()).orElseThrow();
        assertEquals(ClinicalAssessmentStatus.DRAFT, updated.getClinicalAssessmentStatus());
    }
}
