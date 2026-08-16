package com.example.clinical.repository;

import com.example.clinical.domain.entity.TherapyPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TherapyPlanRepository extends JpaRepository<TherapyPlan, UUID> {
    List<TherapyPlan> findByClinicalCase_Id(UUID caseId);
}
