package com.example.clinical.controller;

import com.example.clinical.dto.AssessmentTaskAssignmentCreateRequest;
import com.example.clinical.dto.AssessmentTaskAssignmentResponse;
import com.example.clinical.service.AssessmentTaskAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/encounters/{encounterId}/assignments")
@RequiredArgsConstructor
public class AssessmentTaskAssignmentController {

    private final AssessmentTaskAssignmentService assignmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssessmentTaskAssignmentResponse createAssignment(
            @PathVariable UUID encounterId,
            @RequestBody AssessmentTaskAssignmentCreateRequest request) {
        return assignmentService.createAssignment(encounterId, request);
    }

    @GetMapping
    public List<AssessmentTaskAssignmentResponse> getAssignments(@PathVariable UUID encounterId) {
        return assignmentService.getAssignmentsByEncounter(encounterId);
    }
}
