package com.example.clinical.repository;

import com.example.clinical.domain.entity.ClinicalInterpretationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClinicalInterpretationHistoryRepository extends JpaRepository<ClinicalInterpretationHistory, UUID> {
}
