package com.example.clinical.repository;

import com.example.clinical.domain.entity.FluencyAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FluencyAssessmentRepository extends JpaRepository<FluencyAssessment, UUID> {
    List<FluencyAssessment> findByEncounterId(UUID encounterId);
}
