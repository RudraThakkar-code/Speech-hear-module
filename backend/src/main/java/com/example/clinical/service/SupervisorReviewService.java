package com.example.clinical.service;

import com.example.clinical.domain.enums.ClinicalAssessmentStatus;
import com.example.clinical.dto.SupervisorReviewRequest;
import com.example.clinical.dto.SupervisorReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupervisorReviewService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public SupervisorReviewResponse reviewInterpretation(UUID interpretationId, SupervisorReviewRequest request) {
        UUID reviewId = UUID.randomUUID();
        String action = request.getAction().trim().toUpperCase();

        if (!action.equals("APPROVE") && !action.equals("RETURN_FOR_CORRECTION") && !action.equals("MODIFY")) {
            throw new IllegalArgumentException("Unsupported supervisor action: " + action);
        }

        String currentStatus = jdbcTemplate.queryForObject(
                "SELECT clinical_assessment_status::text FROM clinical_interpretation WHERE clinical_interpretation_id = ?",
                String.class, interpretationId);
        if (currentStatus == null) {
            throw new IllegalArgumentException("Clinical Interpretation not found");
        }
        if (!currentStatus.equals(ClinicalAssessmentStatus.SUBMITTED.name())
                && !currentStatus.equals(ClinicalAssessmentStatus.UNDER_REVIEW.name())) {
            throw new IllegalStateException("Only SUBMITTED or UNDER_REVIEW interpretations can be reviewed");
        }

        jdbcTemplate.update("""
                INSERT INTO supervisor_review
                (review_id, supervisor_id, reviewed_clinical_interpretation_id, review_status, reviewed_at, supervisor_action, supervisor_comments)
                VALUES (?, ?, ?, 'COMPLETED', NOW(), CAST(? AS supervisor_action), ?)
                """, reviewId, request.getSupervisorId(), interpretationId, action, request.getComments());

        int previousVersion = jdbcTemplate.queryForObject(
                "SELECT version FROM clinical_interpretation WHERE clinical_interpretation_id = ?",
                Integer.class, interpretationId);

        jdbcTemplate.update("""
                INSERT INTO clinical_interpretation_history
                (clinical_interpretation_id, version_number, evidence_summary, clinical_assessment_status, recommended_action, snapshot_created_by)
                SELECT clinical_interpretation_id, ?, evidence_summary, clinical_assessment_status, recommended_action, ?
                FROM clinical_interpretation WHERE clinical_interpretation_id = ?
                ON CONFLICT (clinical_interpretation_id, version_number) DO NOTHING
                """, previousVersion, request.getSupervisorId(), interpretationId);

        UUID correctionRequestId = null;
        if (action.equals("APPROVE")) {
            jdbcTemplate.update("UPDATE clinical_interpretation SET clinical_assessment_status = 'FINALIZED'::clinical_assessment_status WHERE clinical_interpretation_id = ?", interpretationId);
        } else if (action.equals("RETURN_FOR_CORRECTION")) {
            if (request.getCorrectionReason() == null || request.getCorrectionReason().isBlank()) {
                throw new IllegalArgumentException("correctionReason is required when returning for correction");
            }
            jdbcTemplate.update("UPDATE clinical_interpretation SET clinical_assessment_status = 'DRAFT'::clinical_assessment_status WHERE clinical_interpretation_id = ?", interpretationId);
            correctionRequestId = UUID.randomUUID();
            jdbcTemplate.update("""
                    INSERT INTO correction_request
                    (correction_request_id, clinical_interpretation_id, supervisor_review_id, requested_by, reason, status)
                    VALUES (?, ?, ?, ?, ?, 'OPEN')
                    """, correctionRequestId, interpretationId, reviewId, request.getSupervisorId(), request.getCorrectionReason());
        } else {
            jdbcTemplate.update("UPDATE clinical_interpretation SET clinical_assessment_status = 'UNDER_REVIEW'::clinical_assessment_status WHERE clinical_interpretation_id = ?", interpretationId);
        }

        jdbcTemplate.update("""
                INSERT INTO supervisor_review_history
                (supervisor_review_id, clinical_interpretation_id, review_status, supervisor_action, supervisor_comments, version_number, recorded_by)
                VALUES (?, ?, 'COMPLETED', CAST(? AS supervisor_action), ?, 1, ?)
                """, reviewId, interpretationId, action, request.getComments(), request.getSupervisorId());

        return SupervisorReviewResponse.builder()
                .reviewId(reviewId)
                .interpretationId(interpretationId)
                .reviewStatus("COMPLETED")
                .action(action)
                .comments(request.getComments())
                .correctionRequestId(correctionRequestId)
                .build();
    }
}
