package com.example.clinical.repository;

import com.example.clinical.domain.entity.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EncounterRepository extends JpaRepository<Encounter, UUID> {
}
