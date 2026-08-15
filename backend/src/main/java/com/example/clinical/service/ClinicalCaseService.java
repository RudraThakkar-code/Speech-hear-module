package com.example.clinical.service;

import com.example.clinical.domain.entity.ClinicalCase;
import com.example.clinical.domain.entity.Patient;
import com.example.clinical.domain.entity.User;
import com.example.clinical.domain.enums.CaseStatus;
import com.example.clinical.dto.CaseCreateRequest;
import com.example.clinical.dto.CaseResponse;
import com.example.clinical.repository.ClinicalCaseRepository;
import com.example.clinical.repository.PatientRepository;
import com.example.clinical.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClinicalCaseService {

    private final ClinicalCaseRepository clinicalCaseRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    public CaseResponse createCase(CaseCreateRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        User therapist = userRepository.findById(request.getAssignedTherapistId())
                .orElseThrow(() -> new IllegalArgumentException("Therapist not found"));

        User supervisor = userRepository.findById(request.getAssignedSupervisorId())
                .orElseThrow(() -> new IllegalArgumentException("Supervisor not found"));

        ClinicalCase clinicalCase = new ClinicalCase();
        clinicalCase.setPatient(patient);
        clinicalCase.setCaseStatus(CaseStatus.ACTIVE);
        clinicalCase.setAssignedTherapist(therapist);
        clinicalCase.setAssignedSupervisor(supervisor);

        ClinicalCase saved = clinicalCaseRepository.save(clinicalCase);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public CaseResponse getCase(UUID caseId) {
        ClinicalCase clinicalCase = clinicalCaseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Case not found"));
        return mapToResponse(clinicalCase);
    }

    private CaseResponse mapToResponse(ClinicalCase clinicalCase) {
        CaseResponse response = new CaseResponse();
        response.setId(clinicalCase.getId());
        response.setPatientId(clinicalCase.getPatient().getId());
        response.setPatientName(clinicalCase.getPatient().getLegalName());
        response.setCaseStatus(clinicalCase.getCaseStatus());
        response.setAssignedTherapistId(clinicalCase.getAssignedTherapist().getId());
        response.setAssignedTherapistName(clinicalCase.getAssignedTherapist().getName());
        response.setAssignedSupervisorId(clinicalCase.getAssignedSupervisor().getId());
        response.setAssignedSupervisorName(clinicalCase.getAssignedSupervisor().getName());
        return response;
    }
}
