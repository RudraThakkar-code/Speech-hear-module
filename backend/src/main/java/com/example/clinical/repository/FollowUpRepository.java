package com.example.clinical.repository;

import com.example.clinical.domain.entity.FollowUp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FollowUpRepository extends JpaRepository<FollowUp, UUID> {
    Optional<FollowUp> findByEncounter_Id(UUID encounterId);
}
