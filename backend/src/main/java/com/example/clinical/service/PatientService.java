package com.example.clinical.service;

import com.example.clinical.domain.entity.Patient;
import com.example.clinical.dto.PatientCreateRequest;
import com.example.clinical.dto.PatientResponse;
import com.example.clinical.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional
    public PatientResponse createPatient(PatientCreateRequest request) {
        Patient patient = new Patient();
        patient.setLegalName(request.getLegalName());
        patient.setPreferredName(request.getPreferredName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setSexAtBirth(request.getSexAtBirth());
        patient.setGenderIdentity(request.getGenderIdentity());
        patient.setContactNumber(request.getContactNumber());
        patient.setEmailAddress(request.getEmailAddress());
        patient.setHomeAddress(request.getHomeAddress());

        Patient saved = patientRepository.save(patient);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatient(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        return mapToResponse(patient);
    }

    private PatientResponse mapToResponse(Patient patient) {
        PatientResponse response = new PatientResponse();
        response.setId(patient.getId());
        response.setLegalName(patient.getLegalName());
        response.setPreferredName(patient.getPreferredName());
        response.setDateOfBirth(patient.getDateOfBirth());
        response.setSexAtBirth(patient.getSexAtBirth());
        response.setGenderIdentity(patient.getGenderIdentity());
        response.setContactNumber(patient.getContactNumber());
        response.setEmailAddress(patient.getEmailAddress());
        response.setHomeAddress(patient.getHomeAddress());
        return response;
    }
}
