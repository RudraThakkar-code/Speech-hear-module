package com.example.clinical.repository;

import com.example.clinical.domain.entity.ProgressRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProgressRecordRepository extends JpaRepository<ProgressRecord, UUID> {
    List<ProgressRecord> findByGoal_Id(UUID goalId);
}
