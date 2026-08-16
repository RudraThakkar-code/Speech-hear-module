package com.example.clinical.repository;

import com.example.clinical.domain.entity.DoctorRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DoctorRecommendationRepository extends JpaRepository<DoctorRecommendation, UUID> {
    List<DoctorRecommendation> findByClinicalCase_IdOrderByCreatedAtDesc(UUID caseId);
}
