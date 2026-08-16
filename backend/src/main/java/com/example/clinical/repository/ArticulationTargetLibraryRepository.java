package com.example.clinical.repository;
import com.example.clinical.domain.entity.ArticulationTargetLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface ArticulationTargetLibraryRepository extends JpaRepository<ArticulationTargetLibrary, UUID> {}
