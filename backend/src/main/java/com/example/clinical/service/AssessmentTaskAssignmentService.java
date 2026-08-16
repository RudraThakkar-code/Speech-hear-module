package com.example.clinical.service;

import com.example.clinical.domain.entity.AssessmentTaskAssignment;
import com.example.clinical.domain.entity.AssessmentTaskLibrary;
import com.example.clinical.domain.entity.Encounter;
import com.example.clinical.domain.entity.User;
import com.example.clinical.dto.AssessmentTaskAssignmentCreateRequest;
import com.example.clinical.dto.AssessmentTaskAssignmentResponse;
import com.example.clinical.repository.AssessmentTaskAssignmentRepository;
import com.example.clinical.repository.AssessmentTaskLibraryRepository;
import com.example.clinical.repository.EncounterRepository;
import com.example.clinical.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentTaskAssignmentService {

    private final AssessmentTaskAssignmentRepository assignmentRepository;
    private final AssessmentTaskLibraryRepository taskRepository;
    private final EncounterRepository encounterRepository;
    private final UserRepository userRepository;

    @Transactional
    public AssessmentTaskAssignmentResponse createAssignment(UUID encounterId, AssessmentTaskAssignmentCreateRequest request) {
        Encounter encounter = encounterRepository.findById(encounterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Encounter not found"));

        AssessmentTaskLibrary task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        User assignedBy = userRepository.findById(request.getAssignedById())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        AssessmentTaskAssignment assignment = new AssessmentTaskAssignment();
        assignment.setEncounter(encounter);
        assignment.setTask(task);
        assignment.setAssignedBy(assignedBy);
        assignment.setSequenceOrder(request.getSequenceOrder());
        assignment.setRequired(request.getRequired());
        assignment.setCompletionStatus(request.getCompletionStatus());

        AssessmentTaskAssignment savedAssignment = assignmentRepository.save(assignment);
        return mapToResponse(savedAssignment);
    }

    public List<AssessmentTaskAssignmentResponse> getAssignmentsByEncounter(UUID encounterId) {
        return assignmentRepository.findByEncounterId(encounterId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AssessmentTaskAssignmentResponse mapToResponse(AssessmentTaskAssignment assignment) {
        AssessmentTaskAssignmentResponse response = new AssessmentTaskAssignmentResponse();
        response.setId(assignment.getId());
        response.setEncounterId(assignment.getEncounter().getId());
        response.setTaskId(assignment.getTask().getId());
        response.setAssignedById(assignment.getAssignedBy().getId());
        response.setSequenceOrder(assignment.getSequenceOrder());
        response.setRequired(assignment.getRequired());
        response.setCompletionStatus(assignment.getCompletionStatus());
        return response;
    }
}
