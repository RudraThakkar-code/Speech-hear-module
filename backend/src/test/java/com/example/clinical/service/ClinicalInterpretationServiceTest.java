package com.example.clinical.service;

import com.example.clinical.domain.entity.ClinicalInterpretation;
import com.example.clinical.domain.entity.ClinicalProblemReference;
import com.example.clinical.domain.entity.Encounter;
import com.example.clinical.domain.enums.ClinicalAssessmentStatus;
import com.example.clinical.domain.enums.ClinicalRecommendedAction;
import com.example.clinical.dto.ClinicalInterpretationRequest;
import com.example.clinical.dto.ClinicalInterpretationResponse;
import com.example.clinical.dto.ProvisionalAssessmentRequest;
import com.example.clinical.dto.ProvisionalAssessmentResponse;
import com.example.clinical.dto.ProvisionalProblemRequest;
import com.example.clinical.repository.ClinicalInterpretationRepository;
import com.example.clinical.repository.ClinicalProblemReferenceRepository;
import com.example.clinical.repository.EncounterRepository;
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
public class ClinicalInterpretationServiceTest {

    @Mock
    private ClinicalInterpretationRepository interpretationRepository;

    @Mock
    private EncounterRepository encounterRepository;

    @Mock
    private ClinicalProblemReferenceRepository problemReferenceRepository;

    @Mock
    private com.example.clinical.repository.ClinicalInterpretationHistoryRepository historyRepository;

    @Mock
    private com.example.clinical.repository.ClinicalInterpretationProblemHistoryRepository problemHistoryRepository;

    @InjectMocks
    private ClinicalInterpretationService service;

    @Test
    void shouldCreateInterpretation() {
        UUID encounterId = UUID.randomUUID();
        Encounter encounter = new Encounter();
        encounter.setId(encounterId);

        ClinicalInterpretationRequest request = new ClinicalInterpretationRequest();
        request.setEvidenceSummary("Test summary");
        request.setClinicalAssessmentStatus(ClinicalAssessmentStatus.DRAFT);
        request.setRecommendedAction(ClinicalRecommendedAction.FURTHER_ASSESSMENT);

        ClinicalInterpretation saved = new ClinicalInterpretation();
        saved.setClinicalInterpretationId(UUID.randomUUID());
        saved.setEvidenceSummary(request.getEvidenceSummary());
        saved.setClinicalAssessmentStatus(request.getClinicalAssessmentStatus());
        saved.setRecommendedAction(request.getRecommendedAction());

        when(encounterRepository.findById(encounterId)).thenReturn(Optional.of(encounter));
        when(interpretationRepository.save(any(ClinicalInterpretation.class))).thenReturn(saved);

        ClinicalInterpretationResponse response = service.createInterpretation(encounterId, request);

        assertNotNull(response);
        assertEquals(saved.getClinicalInterpretationId(), response.getInterpretationId());
        assertEquals("Test summary", response.getEvidenceSummary());
    }

    @Test
    void shouldRecordProvisionalAssessment() {
        UUID interpretationId = UUID.randomUUID();
        ClinicalInterpretation interpretation = new ClinicalInterpretation();
        interpretation.setClinicalInterpretationId(interpretationId);
        interpretation.setClinicalAssessmentStatus(ClinicalAssessmentStatus.DRAFT);
        interpretation.setRecommendedAction(ClinicalRecommendedAction.THERAPY);

        ProvisionalProblemRequest problemReq = new ProvisionalProblemRequest();
        problemReq.setProblemCode("ARTIC");

        ProvisionalAssessmentRequest request = new ProvisionalAssessmentRequest();
        request.setProblems(List.of(problemReq));
        request.setRecommendedAction(ClinicalRecommendedAction.THERAPY);

        ClinicalProblemReference problemRef = new ClinicalProblemReference();
        problemRef.setProblemCode("ARTIC");
        problemRef.setProblemName("Articulation Disorder");

        when(interpretationRepository.findById(interpretationId)).thenReturn(Optional.of(interpretation));
        when(problemReferenceRepository.findById("ARTIC")).thenReturn(Optional.of(problemRef));
        when(interpretationRepository.save(any(ClinicalInterpretation.class))).thenReturn(interpretation);

        ProvisionalAssessmentResponse response = service.recordProvisionalAssessment(interpretationId, request);

        assertNotNull(response);
        assertEquals(1, response.getIdentifiedProblems().size());
        assertEquals("ARTIC", response.getIdentifiedProblems().get(0).getProblemCode());
    }

    @Test
    void shouldThrowExceptionWhenModifyingFinalizedAssessment() {
        UUID interpretationId = UUID.randomUUID();
        ClinicalInterpretation interpretation = new ClinicalInterpretation();
        interpretation.setClinicalInterpretationId(interpretationId);
        interpretation.setClinicalAssessmentStatus(ClinicalAssessmentStatus.FINALIZED);

        ProvisionalAssessmentRequest request = new ProvisionalAssessmentRequest();

        when(interpretationRepository.findById(interpretationId)).thenReturn(Optional.of(interpretation));

        assertThrows(IllegalStateException.class, () -> service.recordProvisionalAssessment(interpretationId, request));
    }

    @Test
    void shouldSubmitInterpretationAndSnapshot() {
        UUID interpretationId = UUID.randomUUID();
        ClinicalInterpretation interpretation = new ClinicalInterpretation();
        interpretation.setClinicalInterpretationId(interpretationId);
        interpretation.setClinicalAssessmentStatus(ClinicalAssessmentStatus.DRAFT);
        interpretation.setVersion(1);

        when(interpretationRepository.findById(interpretationId)).thenReturn(Optional.of(interpretation));
        when(interpretationRepository.save(any())).thenReturn(interpretation);

        ClinicalInterpretationResponse response = service.submitInterpretation(interpretationId);

        assertNotNull(response);
        assertEquals(ClinicalAssessmentStatus.SUBMITTED, response.getClinicalAssessmentStatus());
        org.mockito.Mockito.verify(historyRepository).save(any(com.example.clinical.domain.entity.ClinicalInterpretationHistory.class));
    }
}
