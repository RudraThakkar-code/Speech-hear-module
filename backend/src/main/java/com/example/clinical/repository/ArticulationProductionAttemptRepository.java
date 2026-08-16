package com.example.clinical.repository;

import com.example.clinical.domain.entity.ArticulationProductionAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArticulationProductionAttemptRepository extends JpaRepository<ArticulationProductionAttempt, UUID> {
    List<ArticulationProductionAttempt> findByEncounterId(UUID encounterId);
}
