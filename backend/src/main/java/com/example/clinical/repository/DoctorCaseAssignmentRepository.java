package com.example.clinical.repository;

import com.example.clinical.domain.entity.DoctorCaseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DoctorCaseAssignmentRepository extends JpaRepository<DoctorCaseAssignment, UUID> {
    List<DoctorCaseAssignment> findByClinicalCase_Id(UUID caseId);
    List<DoctorCaseAssignment> findByDoctor_Id(UUID doctorId);
}
