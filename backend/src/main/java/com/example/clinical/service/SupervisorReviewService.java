package com.example.clinical.service;

import com.example.clinical.domain.entity.ClinicalInterpretation;
import com.example.clinical.domain.entity.SupervisorReview;
import com.example.clinical.domain.entity.User;
import com.example.clinical.domain.enums.ClinicalAssessmentStatus;
import com.example.clinical.domain.enums.SupervisorReviewStatus;
import com.example.clinical.dto.SupervisorReviewRequest;
import com.example.clinical.dto.SupervisorReviewResponse;
import com.example.clinical.repository.ClinicalInterpretationRepository;
import com.example.clinical.domain.entity.CorrectionRequest;
import com.example.clinical.domain.entity.SupervisorReviewHistory;
import com.example.clinical.domain.enums.CorrectionStatus;
import com.example.clinical.repository.CorrectionRequestRepository;
import com.example.clinical.repository.SupervisorReviewHistoryRepository;
import com.example.clinical.repository.SupervisorReviewRepository;
import com.example.clinical.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupervisorReviewService {

    private final SupervisorReviewRepository reviewRepository;
    private final ClinicalInterpretationRepository interpretationRepository;
    private final UserRepository userRepository;
    private final SupervisorReviewHistoryRepository supervisorReviewHistoryRepository;
    private final CorrectionRequestRepository correctionRequestRepository;

    @Transactional
    public SupervisorReviewResponse submitReview(UUID interpretationId, SupervisorReviewRequest request) {
        ClinicalInterpretation interpretation = interpretationRepository.findById(interpretationId)
                .orElseThrow(() -> new IllegalArgumentException("Clinical Interpretation not found"));

        if (interpretation.getClinicalAssessmentStatus() != ClinicalAssessmentStatus.SUBMITTED
                && interpretation.getClinicalAssessmentStatus() != ClinicalAssessmentStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Interpretation must be in SUBMITTED or UNDER_REVIEW state for review");
        }

        User supervisor = userRepository.findById(request.getSupervisorId())
                .orElseThrow(() -> new IllegalArgumentException("Supervisor not found"));

        SupervisorReview review = new SupervisorReview();
        review.setSupervisor(supervisor);
        review.setReviewedClinicalInterpretation(interpretation);
        review.setReviewStatus(SupervisorReviewStatus.COMPLETED);
        review.setReviewedAt(OffsetDateTime.now());
        review.setSupervisorAction(request.getAction());
        review.setSupervisorComments(request.getComments());

        SupervisorReview saved = reviewRepository.save(review);

        SupervisorReviewHistory history = new SupervisorReviewHistory();
        history.setSupervisorReview(saved);
        history.setInterpretation(interpretation);
        history.setAction(request.getAction());
        history.setReviewComments(request.getComments());
        history.setReviewer(supervisor);
        history.setVersionNumber(interpretation.getVersion());
        supervisorReviewHistoryRepository.save(history);

        switch (request.getAction()) {
            case APPROVE:
                interpretation.setClinicalAssessmentStatus(ClinicalAssessmentStatus.FINALIZED);
                break;
            case RETURN_FOR_CORRECTION:
                interpretation.setClinicalAssessmentStatus(ClinicalAssessmentStatus.DRAFT);

                CorrectionRequest correctionRequest = new CorrectionRequest();
                correctionRequest.setInterpretation(interpretation);
                correctionRequest.setSupervisorReview(saved);
                correctionRequest.setRequestedBy(supervisor);
                correctionRequest.setReason(request.getComments());
                correctionRequest.setStatus(CorrectionStatus.OPEN);
                correctionRequestRepository.save(correctionRequest);
                break;
            case MODIFY:
                interpretation.setClinicalAssessmentStatus(ClinicalAssessmentStatus.UNDER_REVIEW);
                break;
        }

        interpretationRepository.save(interpretation);

        return mapToResponse(saved);
    }

    private SupervisorReviewResponse mapToResponse(SupervisorReview review) {
        return SupervisorReviewResponse.builder()
                .reviewId(review.getReviewId())
                .reviewedInterpretationId(review.getReviewedClinicalInterpretation().getClinicalInterpretationId())
                .status(review.getReviewStatus())
                .action(review.getSupervisorAction())
                .comments(review.getSupervisorComments())
                .reviewedAt(review.getReviewedAt())
                .build();
    }
}
