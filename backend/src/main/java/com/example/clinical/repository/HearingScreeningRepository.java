package com.example.clinical.repository;

import com.example.clinical.domain.entity.HearingScreening;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HearingScreeningRepository extends JpaRepository<HearingScreening, UUID> {
}
