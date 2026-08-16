package com.example.clinical.service;

import com.example.clinical.domain.entity.ClinicalInterpretation;
import com.example.clinical.domain.entity.SupervisorReview;
import com.example.clinical.domain.entity.User;
import com.example.clinical.domain.enums.ClinicalAssessmentStatus;
import com.example.clinical.domain.enums.SupervisorAction;
import com.example.clinical.domain.enums.SupervisorReviewStatus;
import com.example.clinical.dto.SupervisorReviewRequest;
import com.example.clinical.dto.SupervisorReviewResponse;
import com.example.clinical.repository.ClinicalInterpretationRepository;
import com.example.clinical.repository.SupervisorReviewRepository;
import com.example.clinical.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SupervisorReviewServiceTest {

    @Mock
    private SupervisorReviewRepository reviewRepository;

    @Mock
    private ClinicalInterpretationRepository interpretationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private com.example.clinical.repository.SupervisorReviewHistoryRepository supervisorReviewHistoryRepository;

    @Mock
    private com.example.clinical.repository.CorrectionRequestRepository correctionRequestRepository;

    @InjectMocks
    private SupervisorReviewService service;

    @Test
    void shouldSubmitReviewAndApprove() {
        UUID interpretationId = UUID.randomUUID();
        UUID supervisorId = UUID.randomUUID();

        ClinicalInterpretation interpretation = new ClinicalInterpretation();
        interpretation.setClinicalInterpretationId(interpretationId);
        interpretation.setClinicalAssessmentStatus(ClinicalAssessmentStatus.SUBMITTED);

        User supervisor = new User();
        supervisor.setId(supervisorId);

        SupervisorReviewRequest request = new SupervisorReviewRequest();
        request.setSupervisorId(supervisorId);
        request.setAction(SupervisorAction.APPROVE);
        request.setComments("Looks good");

        SupervisorReview review = new SupervisorReview();
        review.setReviewId(UUID.randomUUID());
        review.setReviewedClinicalInterpretation(interpretation);
        review.setReviewStatus(SupervisorReviewStatus.COMPLETED);
        review.setSupervisorAction(SupervisorAction.APPROVE);
        review.setSupervisorComments("Looks good");
        review.setReviewedAt(OffsetDateTime.now());

        when(interpretationRepository.findById(interpretationId)).thenReturn(Optional.of(interpretation));
        when(userRepository.findById(supervisorId)).thenReturn(Optional.of(supervisor));
        when(reviewRepository.save(any(SupervisorReview.class))).thenReturn(review);

        SupervisorReviewResponse response = service.submitReview(interpretationId, request);

        assertNotNull(response);
        assertEquals(SupervisorAction.APPROVE, response.getAction());
        assertEquals(ClinicalAssessmentStatus.FINALIZED, interpretation.getClinicalAssessmentStatus());
        org.mockito.Mockito.verify(supervisorReviewHistoryRepository).save(any());
    }

    @Test
    void shouldSubmitReviewAndReturnForCorrection() {
        UUID interpretationId = UUID.randomUUID();
        UUID supervisorId = UUID.randomUUID();

        ClinicalInterpretation interpretation = new ClinicalInterpretation();
        interpretation.setClinicalInterpretationId(interpretationId);
        interpretation.setClinicalAssessmentStatus(ClinicalAssessmentStatus.SUBMITTED);

        User supervisor = new User();
        supervisor.setId(supervisorId);

        SupervisorReviewRequest request = new SupervisorReviewRequest();
        request.setSupervisorId(supervisorId);
        request.setAction(SupervisorAction.RETURN_FOR_CORRECTION);

        SupervisorReview review = new SupervisorReview();
        review.setReviewId(UUID.randomUUID());
        review.setReviewedClinicalInterpretation(interpretation);
        review.setReviewStatus(SupervisorReviewStatus.COMPLETED);
        review.setSupervisorAction(SupervisorAction.RETURN_FOR_CORRECTION);
        review.setReviewedAt(OffsetDateTime.now());

        when(interpretationRepository.findById(interpretationId)).thenReturn(Optional.of(interpretation));
        when(userRepository.findById(supervisorId)).thenReturn(Optional.of(supervisor));
        when(reviewRepository.save(any(SupervisorReview.class))).thenReturn(review);

        SupervisorReviewResponse response = service.submitReview(interpretationId, request);

        assertNotNull(response);
        assertEquals(SupervisorAction.RETURN_FOR_CORRECTION, response.getAction());
        assertEquals(ClinicalAssessmentStatus.DRAFT, interpretation.getClinicalAssessmentStatus());
        org.mockito.Mockito.verify(correctionRequestRepository).save(any());
        org.mockito.Mockito.verify(supervisorReviewHistoryRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenInterpretationNotInReviewableState() {
        UUID interpretationId = UUID.randomUUID();
        ClinicalInterpretation interpretation = new ClinicalInterpretation();
        interpretation.setClinicalInterpretationId(interpretationId);
        interpretation.setClinicalAssessmentStatus(ClinicalAssessmentStatus.DRAFT); // Invalid state for review

        SupervisorReviewRequest request = new SupervisorReviewRequest();

        when(interpretationRepository.findById(interpretationId)).thenReturn(Optional.of(interpretation));

        assertThrows(IllegalStateException.class, () -> service.submitReview(interpretationId, request));
    }
}
