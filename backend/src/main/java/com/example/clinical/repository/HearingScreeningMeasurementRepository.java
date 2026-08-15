package com.example.clinical.repository;

import com.example.clinical.domain.entity.HearingScreeningMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HearingScreeningMeasurementRepository extends JpaRepository<HearingScreeningMeasurement, UUID> {
}
