package com.example.clinical.repository;

import com.example.clinical.domain.entity.DevelopmentalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DevelopmentalHistoryRepository extends JpaRepository<DevelopmentalHistory, UUID> {
}
