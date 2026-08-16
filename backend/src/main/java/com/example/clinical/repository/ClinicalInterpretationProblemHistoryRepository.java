package com.example.clinical.repository;

import com.example.clinical.domain.entity.ClinicalInterpretationProblemHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClinicalInterpretationProblemHistoryRepository extends JpaRepository<ClinicalInterpretationProblemHistory, UUID> {
}
