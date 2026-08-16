package com.example.clinical.controller;

import com.example.clinical.dto.SupervisorReviewRequest;
import com.example.clinical.dto.SupervisorReviewResponse;
import com.example.clinical.service.SupervisorReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clinical-interpretations")
@RequiredArgsConstructor
public class SupervisorReviewController {

    private final SupervisorReviewService reviewService;

    @PostMapping("/{interpretationId}/reviews")
    public ResponseEntity<SupervisorReviewResponse> submitReview(
            @PathVariable UUID interpretationId,
            @Valid @RequestBody SupervisorReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.submitReview(interpretationId, request));
    }
}
