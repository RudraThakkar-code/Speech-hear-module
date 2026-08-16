package com.example.clinical.repository;

import com.example.clinical.domain.entity.SpeechSample;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import com.example.clinical.domain.entity.Encounter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SpeechSampleRepository extends JpaRepository<SpeechSample, UUID> {
    @Query("SELECT s FROM SpeechSample s WHERE s.assignment.encounter = :encounter")
    List<SpeechSample> findByEncounter(@Param("encounter") Encounter encounter);
}
