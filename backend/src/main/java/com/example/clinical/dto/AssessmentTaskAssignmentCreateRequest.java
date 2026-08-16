package com.example.clinical.dto;

import com.example.clinical.domain.enums.AssignmentStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class AssessmentTaskAssignmentCreateRequest {
    private UUID taskId;
    private UUID assignedById;
    private Integer sequenceOrder;
    private Boolean required;
    private AssignmentStatus completionStatus;
}
