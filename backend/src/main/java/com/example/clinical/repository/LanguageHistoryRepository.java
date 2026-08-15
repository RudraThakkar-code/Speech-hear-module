package com.example.clinical.repository;

import com.example.clinical.domain.entity.LanguageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface LanguageHistoryRepository extends JpaRepository<LanguageHistory, UUID> {
}
