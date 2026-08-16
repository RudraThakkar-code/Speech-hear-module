package com.example.clinical.repository;
import com.example.clinical.domain.entity.AssessmentTaskLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface AssessmentTaskLibraryRepository extends JpaRepository<AssessmentTaskLibrary, UUID> {}
