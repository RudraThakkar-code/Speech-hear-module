package com.example.clinical.repository;

import com.example.clinical.domain.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
}
