package com.example.clinical.repository;

import com.example.clinical.domain.entity.TherapyGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TherapyGoalRepository extends JpaRepository<TherapyGoal, UUID> {
    List<TherapyGoal> findByClinicalCase_Id(UUID caseId);
}
