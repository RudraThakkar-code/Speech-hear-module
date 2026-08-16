package com.example.clinical.repository;

import com.example.clinical.domain.entity.SpeechSample;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpeechSampleRepository extends JpaRepository<SpeechSample, UUID> {}
