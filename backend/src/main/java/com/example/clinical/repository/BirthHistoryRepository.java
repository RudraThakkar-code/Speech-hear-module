package com.example.clinical.repository;

import com.example.clinical.domain.entity.BirthHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BirthHistoryRepository extends JpaRepository<BirthHistory, UUID> {
}
