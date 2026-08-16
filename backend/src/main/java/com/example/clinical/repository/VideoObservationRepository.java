package com.example.clinical.repository;

import com.example.clinical.domain.entity.VideoObservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VideoObservationRepository extends JpaRepository<VideoObservation, UUID> {
    List<VideoObservation> findByEncounter_Id(UUID encounterId);
}
