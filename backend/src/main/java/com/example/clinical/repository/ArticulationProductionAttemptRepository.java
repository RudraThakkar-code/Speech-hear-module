package com.example.clinical.repository;
import com.example.clinical.domain.entity.ArticulationProductionAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import com.example.clinical.domain.entity.Encounter;
import java.util.List;
public interface ArticulationProductionAttemptRepository extends JpaRepository<ArticulationProductionAttempt, UUID> {
    List<ArticulationProductionAttempt> findByEncounter(Encounter encounter);
}
