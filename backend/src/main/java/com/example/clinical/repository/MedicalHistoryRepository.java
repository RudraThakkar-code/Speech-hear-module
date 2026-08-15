package com.example.clinical.repository;

import com.example.clinical.domain.entity.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, UUID> {
}
