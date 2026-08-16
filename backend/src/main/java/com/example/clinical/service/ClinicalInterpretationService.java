package com.example.clinical.service;

import com.example.clinical.domain.entity.ClinicalInterpretation;
import com.example.clinical.domain.entity.ClinicalInterpretationProblem;
import com.example.clinical.domain.entity.ClinicalProblemReference;
import com.example.clinical.domain.entity.Encounter;
import com.example.clinical.domain.enums.ClinicalAssessmentStatus;
import com.example.clinical.dto.ClinicalInterpretationRequest;
import com.example.clinical.dto.ClinicalInterpretationResponse;
import com.example.clinical.dto.ProvisionalAssessmentRequest;
import com.example.clinical.dto.ProvisionalAssessmentResponse;
import com.example.clinical.dto.ProvisionalProblemRequest;
import com.example.clinical.dto.ProvisionalProblemResponse;
import com.example.clinical.repository.ClinicalInterpretationRepository;
import com.example.clinical.repository.ClinicalProblemReferenceRepository;
import com.example.clinical.domain.entity.ClinicalInterpretationHistory;
import com.example.clinical.domain.entity.ClinicalInterpretationProblemHistory;
import com.example.clinical.repository.ClinicalInterpretationHistoryRepository;
import com.example.clinical.repository.ClinicalInterpretationProblemHistoryRepository;
import com.example.clinical.repository.EncounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClinicalInterpretationService {

    private final ClinicalInterpretationRepository interpretationRepository;
    private final EncounterRepository encounterRepository;
    private final ClinicalProblemReferenceRepository problemReferenceRepository;
    private final ClinicalInterpretationHistoryRepository historyRepository;
    private final ClinicalInterpretationProblemHistoryRepository problemHistoryRepository;

    @Transactional
    public ClinicalInterpretationResponse createInterpretation(UUID encounterId, ClinicalInterpretationRequest request) {
        Encounter encounter = encounterRepository.findById(encounterId)
                .orElseThrow(() -> new IllegalArgumentException("Encounter not found"));

        ClinicalInterpretation interpretation = new ClinicalInterpretation();
        interpretation.setEncounter(encounter);
        interpretation.setEvidenceSummary(request.getEvidenceSummary());
        interpretation.setClinicalAssessmentStatus(request.getClinicalAssessmentStatus());
        interpretation.setRecommendedAction(request.getRecommendedAction());

        ClinicalInterpretation saved = interpretationRepository.save(interpretation);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public ClinicalInterpretationResponse getInterpretation(UUID interpretationId) {
        ClinicalInterpretation interpretation = interpretationRepository.findById(interpretationId)
                .orElseThrow(() -> new IllegalArgumentException("Clinical Interpretation not found"));
        return mapToResponse(interpretation);
    }

    @Transactional
    public ProvisionalAssessmentResponse recordProvisionalAssessment(UUID interpretationId, ProvisionalAssessmentRequest request) {
        ClinicalInterpretation interpretation = interpretationRepository.findById(interpretationId)
                .orElseThrow(() -> new IllegalArgumentException("Clinical Interpretation not found"));

        if (interpretation.getClinicalAssessmentStatus() == ClinicalAssessmentStatus.FINALIZED) {
            throw new IllegalStateException("Cannot modify a finalized clinical interpretation");
        }

        snapshotInterpretation(interpretation);

        interpretation.setRecommendedAction(request.getRecommendedAction());

        // Clear existing problems
        Set<ClinicalInterpretationProblem> existingProblems = Set.copyOf(interpretation.getProblems());
        existingProblems.forEach(interpretation::removeProblem);

        // Add new problems
        for (ProvisionalProblemRequest problemReq : request.getProblems()) {
            ClinicalProblemReference problemRef = problemReferenceRepository.findById(problemReq.getProblemCode())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid clinical problem code: " + problemReq.getProblemCode()));

            ClinicalInterpretationProblem problem = new ClinicalInterpretationProblem(interpretation, problemRef);
            interpretation.addProblem(problem);
        }

        ClinicalInterpretation saved = interpretationRepository.save(interpretation);

        return mapToProvisionalResponse(saved);
    }

    @Transactional
    public ClinicalInterpretationResponse submitInterpretation(UUID interpretationId) {
        ClinicalInterpretation interpretation = interpretationRepository.findById(interpretationId)
                .orElseThrow(() -> new IllegalArgumentException("Clinical Interpretation not found"));

        if (interpretation.getClinicalAssessmentStatus() != ClinicalAssessmentStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT interpretations can be submitted");
        }

        snapshotInterpretation(interpretation);
        interpretation.setClinicalAssessmentStatus(ClinicalAssessmentStatus.SUBMITTED);

        ClinicalInterpretation saved = interpretationRepository.save(interpretation);
        return mapToResponse(saved);
    }

    private void snapshotInterpretation(ClinicalInterpretation interpretation) {
        ClinicalInterpretationHistory history = new ClinicalInterpretationHistory();
        history.setInterpretation(interpretation);
        history.setVersionNumber(interpretation.getVersion());
        history.setEvidenceSummary(interpretation.getEvidenceSummary());
        history.setClinicalAssessmentStatus(interpretation.getClinicalAssessmentStatus());
        history.setRecommendedAction(interpretation.getRecommendedAction());
        history.setCreatedBy(interpretation.getCreatedBy());
        historyRepository.save(history);

        for (ClinicalInterpretationProblem problem : interpretation.getProblems()) {
            ClinicalInterpretationProblemHistory probHistory = new ClinicalInterpretationProblemHistory();
            probHistory.setInterpretation(interpretation);
            probHistory.setProblem(problem.getProblem());
            probHistory.setVersionNumber(interpretation.getVersion());
            // Since problem associations don't have createdBy naturally in our model right now, we use interpretation's
            probHistory.setCreatedBy(interpretation.getCreatedBy());
            problemHistoryRepository.save(probHistory);
        }
    }

    private ClinicalInterpretationResponse mapToResponse(ClinicalInterpretation interpretation) {
        return ClinicalInterpretationResponse.builder()
                .interpretationId(interpretation.getClinicalInterpretationId())
                .evidenceSummary(interpretation.getEvidenceSummary())
                .clinicalAssessmentStatus(interpretation.getClinicalAssessmentStatus())
                .recommendedAction(interpretation.getRecommendedAction())
                .build();
    }

    private ProvisionalAssessmentResponse mapToProvisionalResponse(ClinicalInterpretation interpretation) {
        List<ProvisionalProblemResponse> identifiedProblems = interpretation.getProblems().stream()
                .map(p -> ProvisionalProblemResponse.builder()
                        .problemCode(p.getProblem().getProblemCode())
                        .problemName(p.getProblem().getProblemName())
                        .build())
                .collect(Collectors.toList());

        return ProvisionalAssessmentResponse.builder()
                .interpretationId(interpretation.getClinicalInterpretationId())
                .evidenceSummary(interpretation.getEvidenceSummary())
                .assessmentStatus(interpretation.getClinicalAssessmentStatus())
                .recommendedAction(interpretation.getRecommendedAction())
                .identifiedProblems(identifiedProblems)
                .build();
    }
}
