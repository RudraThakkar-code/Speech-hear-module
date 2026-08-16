package com.example.clinical.repository;

import com.example.clinical.domain.entity.HomePractice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HomePracticeRepository extends JpaRepository<HomePractice, UUID> {
    List<HomePractice> findBySession_Id(UUID sessionId);
}
