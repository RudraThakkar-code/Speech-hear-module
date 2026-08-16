package com.example.clinical.repository;

import com.example.clinical.domain.entity.HearingScreening;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import com.example.clinical.domain.entity.Encounter;
import java.util.List;

public interface HearingScreeningRepository extends JpaRepository<HearingScreening, UUID> {
    List<HearingScreening> findByEncounter(Encounter encounter);
}
