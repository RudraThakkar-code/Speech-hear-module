package com.example.clinical.repository;

import com.example.clinical.domain.entity.TherapySession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TherapySessionRepository extends JpaRepository<TherapySession, UUID> {
    Optional<TherapySession> findByEncounter_Id(UUID encounterId);
}
