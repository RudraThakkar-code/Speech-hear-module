package com.example.clinical.repository;

import com.example.clinical.domain.entity.ArticulationTargetLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArticulationTargetLibraryRepository extends JpaRepository<ArticulationTargetLibrary, UUID> {
}
