package com.example.clinical.repository;

import com.example.clinical.domain.entity.ClinicalCase;
import com.example.clinical.domain.entity.Encounter;
import com.example.clinical.domain.entity.Patient;
import com.example.clinical.domain.enums.AssessmentReason;
import com.example.clinical.domain.enums.CaseStatus;
import com.example.clinical.domain.enums.ClinicalAssessmentStatus;
import com.example.clinical.domain.enums.ClinicalRecommendedAction;
import com.example.clinical.domain.enums.EncounterType;
import com.example.clinical.dto.ClinicalInterpretationRequest;
import com.example.clinical.dto.ClinicalInterpretationResponse;
import com.example.clinical.dto.ProvisionalAssessmentRequest;
import com.example.clinical.dto.ProvisionalAssessmentResponse;
import com.example.clinical.dto.ProvisionalProblemRequest;
import com.example.clinical.service.ClinicalInterpretationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ClinicalInterpretationIntegrationTest {

    @Autowired
    private ClinicalInterpretationService service;

    @Autowired
    private EncounterRepository encounterRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ClinicalCaseRepository clinicalCaseRepository;

    @Autowired
    private ClinicalProblemReferenceRepository problemReferenceRepository;

    private Encounter testEncounter;

    @BeforeEach
    void setup() {
        Patient patient = new Patient();
        patient.setLegalName("Test Patient");
        patient.setDateOfBirth(LocalDate.of(2015, 1, 1));
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
        testEncounter = encounterRepository.save(encounter);

        com.example.clinical.domain.entity.ClinicalProblemReference problem = new com.example.clinical.domain.entity.ClinicalProblemReference();
        problem.setProblemCode("ARTIC");
        problem.setProblemName("Articulation Disorder");
        problemReferenceRepository.save(problem);
    }

    @Test
    void shouldFailWhenMissingEncounter() {
        ClinicalInterpretationRequest request = new ClinicalInterpretationRequest();
        request.setEvidenceSummary("Summary");
        request.setClinicalAssessmentStatus(ClinicalAssessmentStatus.DRAFT);
        request.setRecommendedAction(ClinicalRecommendedAction.THERAPY);

        assertThrows(IllegalArgumentException.class, () -> service.createInterpretation(UUID.randomUUID(), request));
    }

    @Test
    void shouldFailWithInvalidClinicalProblem() {
        ClinicalInterpretationRequest req = new ClinicalInterpretationRequest("Sum", ClinicalAssessmentStatus.DRAFT, ClinicalRecommendedAction.THERAPY);
        ClinicalInterpretationResponse interpretation = service.createInterpretation(testEncounter.getId(), req);

        ProvisionalAssessmentRequest provReq = new ProvisionalAssessmentRequest();
        provReq.setProblems(List.of(new ProvisionalProblemRequest("INVALID_CODE")));
        provReq.setRecommendedAction(ClinicalRecommendedAction.THERAPY);

        assertThrows(IllegalArgumentException.class, () -> service.recordProvisionalAssessment(interpretation.getInterpretationId(), provReq));
    }

    @Test
    void shouldProgressStatusAndPreventModifyingFinalized() {
        ClinicalInterpretationRequest req = new ClinicalInterpretationRequest("Sum", ClinicalAssessmentStatus.DRAFT, ClinicalRecommendedAction.THERAPY);
        ClinicalInterpretationResponse interpretation = service.createInterpretation(testEncounter.getId(), req);

        // Note: Full status progression would require service methods or setters, which we can simulate by saving
        // In a real scenario we'd use a dedicated endpoint to update status. For now we will rely on creating it as FINALIZED to test the failure.
        ClinicalInterpretationRequest finalizedReq = new ClinicalInterpretationRequest("Sum", ClinicalAssessmentStatus.FINALIZED, ClinicalRecommendedAction.THERAPY);
        ClinicalInterpretationResponse finalizedInterpretation = service.createInterpretation(testEncounter.getId(), finalizedReq);

        ProvisionalAssessmentRequest provReq = new ProvisionalAssessmentRequest();
        provReq.setProblems(List.of(new ProvisionalProblemRequest("ARTIC")));
        provReq.setRecommendedAction(ClinicalRecommendedAction.THERAPY);

        assertThrows(IllegalStateException.class, () -> service.recordProvisionalAssessment(finalizedInterpretation.getInterpretationId(), provReq));
    }

    @Test
    void shouldExecuteFullSuccessFlow() {
        // Create interpretation
        ClinicalInterpretationRequest req = new ClinicalInterpretationRequest("All evidence shows articulation issues.", ClinicalAssessmentStatus.DRAFT, ClinicalRecommendedAction.FURTHER_ASSESSMENT);
        ClinicalInterpretationResponse interpretation = service.createInterpretation(testEncounter.getId(), req);

        assertNotNull(interpretation.getInterpretationId());

        // Create provisional assessment
        ProvisionalAssessmentRequest provReq = new ProvisionalAssessmentRequest();
        provReq.setProblems(List.of(new ProvisionalProblemRequest("ARTIC"))); // ARTIC should exist from seed
        provReq.setRecommendedAction(ClinicalRecommendedAction.THERAPY);

        ProvisionalAssessmentResponse provRes = service.recordProvisionalAssessment(interpretation.getInterpretationId(), provReq);

        assertNotNull(provRes);
        assertEquals(1, provRes.getIdentifiedProblems().size());
        assertEquals("ARTIC", provRes.getIdentifiedProblems().get(0).getProblemCode());
        assertEquals(ClinicalRecommendedAction.THERAPY, provRes.getRecommendedAction());
    }
}
