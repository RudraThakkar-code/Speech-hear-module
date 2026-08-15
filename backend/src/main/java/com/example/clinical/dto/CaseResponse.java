package com.example.clinical.dto;

import com.example.clinical.domain.enums.CaseStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class CaseResponse {
    private UUID id;
    private UUID patientId;
    private String patientName;
    private CaseStatus caseStatus;
    private UUID assignedTherapistId;
    private String assignedTherapistName;
    private UUID assignedSupervisorId;
    private String assignedSupervisorName;
}
