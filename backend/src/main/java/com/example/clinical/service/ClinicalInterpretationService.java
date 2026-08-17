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
import com.example.clinical.repository.EncounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public ClinicalInterpretationResponse createInterpretation(UUID encounterId, ClinicalInterpretationRequest request) {
        Encounter encounter = encounterRepository.findById(encounterId)
                .orElseThrow(() -> new IllegalArgumentException("Encounter not found"));

        ClinicalInterpretation interpretation = new ClinicalInterpretation();
        interpretation.setEncounter(encounter);
        interpretation.setEvidenceSummary(request.getEvidenceSummary());
        interpretation.setClinicalAssessmentStatus(request.getClinicalAssessmentStatus());
        interpretation.setRecommendedAction(request.getRecommendedAction());

        return mapToResponse(interpretationRepository.save(interpretation));
    }

    @Transactional(readOnly = true)
    public ClinicalInterpretationResponse getInterpretation(UUID interpretationId) {
        ClinicalInterpretation interpretation = interpretationRepository.findById(interpretationId)
                .orElseThrow(() -> new IllegalArgumentException("Clinical Interpretation not found"));
        return mapToResponse(interpretation);
    }

    @Transactional
    public ClinicalInterpretationResponse submitInterpretation(UUID interpretationId) {
        ClinicalInterpretation interpretation = getRequired(interpretationId);
        if (interpretation.getClinicalAssessmentStatus() != ClinicalAssessmentStatus.DRAFT) {
            throw new IllegalStateException("Only a DRAFT clinical interpretation can be submitted");
        }
        snapshot(interpretation);
        interpretation.setClinicalAssessmentStatus(ClinicalAssessmentStatus.SUBMITTED);
        return mapToResponse(interpretationRepository.save(interpretation));
    }

    @Transactional
    public ProvisionalAssessmentResponse recordProvisionalAssessment(UUID interpretationId, ProvisionalAssessmentRequest request) {
        ClinicalInterpretation interpretation = getRequired(interpretationId);

        if (interpretation.getClinicalAssessmentStatus() == ClinicalAssessmentStatus.FINALIZED) {
            throw new IllegalStateException("Cannot modify a finalized clinical interpretation");
        }
        if (interpretation.getClinicalAssessmentStatus() == ClinicalAssessmentStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Cannot modify a clinical interpretation under review");
        }

        snapshot(interpretation);
        interpretation.setRecommendedAction(request.getRecommendedAction());

        Set<ClinicalInterpretationProblem> existingProblems = Set.copyOf(interpretation.getProblems());
        existingProblems.forEach(interpretation::removeProblem);

        for (ProvisionalProblemRequest problemReq : request.getProblems()) {
            ClinicalProblemReference problemRef = problemReferenceRepository.findById(problemReq.getProblemCode())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid clinical problem code: " + problemReq.getProblemCode()));
            interpretation.addProblem(new ClinicalInterpretationProblem(interpretation, problemRef));
        }

        return mapToProvisionalResponse(interpretationRepository.save(interpretation));
    }

    private ClinicalInterpretation getRequired(UUID id) {
        return interpretationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Clinical Interpretation not found"));
    }

    private void snapshot(ClinicalInterpretation interpretation) {
        int version = interpretation.getVersion() == null ? 1 : interpretation.getVersion();

        jdbcTemplate.update("""
                INSERT INTO clinical_interpretation_history
                (clinical_interpretation_id, version_number, evidence_summary, clinical_assessment_status, recommended_action)
                SELECT ?, ?, ?, CAST(? AS clinical_assessment_status), CAST(? AS clinical_recommended_action)
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM clinical_interpretation_history
                    WHERE clinical_interpretation_id = ? AND version_number = ?
                )
                """,
                interpretation.getClinicalInterpretationId(), version,
                interpretation.getEvidenceSummary(),
                interpretation.getClinicalAssessmentStatus().name(),
                interpretation.getRecommendedAction().name(),
                interpretation.getClinicalInterpretationId(), version);

        jdbcTemplate.update("""
                INSERT INTO clinical_interpretation_problem_history
                (clinical_interpretation_id, version_number, problem_code)
                SELECT cip.clinical_interpretation_id, ?, cip.problem_code
                FROM clinical_interpretation_problem cip
                WHERE cip.clinical_interpretation_id = ?
                  AND NOT EXISTS (
                      SELECT 1
                      FROM clinical_interpretation_problem_history ciph
                      WHERE ciph.clinical_interpretation_id = cip.clinical_interpretation_id
                        AND ciph.version_number = ?
                        AND ciph.problem_code = cip.problem_code
                  )
                """, version, interpretation.getClinicalInterpretationId(), version);
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
