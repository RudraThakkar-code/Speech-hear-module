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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicalCaseServiceTest {

    @Mock
    private ClinicalCaseRepository clinicalCaseRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ClinicalCaseService clinicalCaseService;

    @Test
    void shouldCreateCaseWhenAllDependenciesExist() {
        // Arrange
        UUID patientId = UUID.randomUUID();
        UUID therapistId = UUID.randomUUID();
        UUID supervisorId = UUID.randomUUID();
        UUID caseId = UUID.randomUUID();

        CaseCreateRequest request = new CaseCreateRequest();
        request.setPatientId(patientId);
        request.setAssignedTherapistId(therapistId);
        request.setAssignedSupervisorId(supervisorId);

        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setLegalName("John Doe");

        User therapist = new User();
        therapist.setId(therapistId);
        therapist.setName("Dr. Therapist");

        User supervisor = new User();
        supervisor.setId(supervisorId);
        supervisor.setName("Dr. Supervisor");

        ClinicalCase savedCase = new ClinicalCase();
        savedCase.setId(caseId);
        savedCase.setPatient(patient);
        savedCase.setAssignedTherapist(therapist);
        savedCase.setAssignedSupervisor(supervisor);
        savedCase.setCaseStatus(CaseStatus.ACTIVE);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userRepository.findById(therapistId)).thenReturn(Optional.of(therapist));
        when(userRepository.findById(supervisorId)).thenReturn(Optional.of(supervisor));
        when(clinicalCaseRepository.save(any(ClinicalCase.class))).thenReturn(savedCase);

        // Act
        CaseResponse response = clinicalCaseService.createCase(request);

        // Assert
        assertNotNull(response);
        assertEquals(caseId, response.getId());
        assertEquals(patientId, response.getPatientId());
        assertEquals("John Doe", response.getPatientName());
        assertEquals(therapistId, response.getAssignedTherapistId());
        assertEquals("Dr. Therapist", response.getAssignedTherapistName());
        assertEquals(supervisorId, response.getAssignedSupervisorId());
        assertEquals("Dr. Supervisor", response.getAssignedSupervisorName());
        assertEquals(CaseStatus.ACTIVE, response.getCaseStatus());

        verify(patientRepository).findById(patientId);
        verify(userRepository).findById(therapistId);
        verify(userRepository).findById(supervisorId);
        verify(clinicalCaseRepository).save(any(ClinicalCase.class));
    }

    @Test
    void shouldThrowWhenPatientNotFound() {
        // Arrange
        UUID patientId = UUID.randomUUID();
        CaseCreateRequest request = new CaseCreateRequest();
        request.setPatientId(patientId);

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> clinicalCaseService.createCase(request));

        assertEquals("Patient not found", exception.getMessage());

        verify(patientRepository).findById(patientId);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(clinicalCaseRepository);
    }

    @Test
    void shouldThrowWhenTherapistNotFound() {
        // Arrange
        UUID patientId = UUID.randomUUID();
        UUID therapistId = UUID.randomUUID();

        CaseCreateRequest request = new CaseCreateRequest();
        request.setPatientId(patientId);
        request.setAssignedTherapistId(therapistId);

        Patient patient = new Patient();
        patient.setId(patientId);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userRepository.findById(therapistId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> clinicalCaseService.createCase(request));

        assertEquals("Therapist not found", exception.getMessage());

        verify(patientRepository).findById(patientId);
        verify(userRepository).findById(therapistId);
        verifyNoInteractions(clinicalCaseRepository);
    }

    @Test
    void shouldThrowWhenSupervisorNotFound() {
        // Arrange
        UUID patientId = UUID.randomUUID();
        UUID therapistId = UUID.randomUUID();
        UUID supervisorId = UUID.randomUUID();

        CaseCreateRequest request = new CaseCreateRequest();
        request.setPatientId(patientId);
        request.setAssignedTherapistId(therapistId);
        request.setAssignedSupervisorId(supervisorId);

        Patient patient = new Patient();
        patient.setId(patientId);

        User therapist = new User();
        therapist.setId(therapistId);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userRepository.findById(therapistId)).thenReturn(Optional.of(therapist));
        when(userRepository.findById(supervisorId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> clinicalCaseService.createCase(request));

        assertEquals("Supervisor not found", exception.getMessage());

        verify(patientRepository).findById(patientId);
        verify(userRepository).findById(therapistId);
        verify(userRepository).findById(supervisorId);
        verifyNoInteractions(clinicalCaseRepository);
    }
}
