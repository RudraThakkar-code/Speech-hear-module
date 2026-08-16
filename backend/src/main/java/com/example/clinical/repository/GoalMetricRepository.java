package com.example.clinical.repository;

import com.example.clinical.domain.entity.GoalMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GoalMetricRepository extends JpaRepository<GoalMetric, UUID> {
    List<GoalMetric> findByGoal_Id(UUID goalId);
}
