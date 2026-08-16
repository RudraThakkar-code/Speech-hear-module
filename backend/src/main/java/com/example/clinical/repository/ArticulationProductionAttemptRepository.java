package com.example.clinical.repository;
import com.example.clinical.domain.entity.ArticulationProductionAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface ArticulationProductionAttemptRepository extends JpaRepository<ArticulationProductionAttempt, UUID> {}
