package com.example.clinical.repository;

import com.example.clinical.domain.entity.FamilyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FamilyHistoryRepository extends JpaRepository<FamilyHistory, UUID> {
}
