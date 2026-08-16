package com.example.clinical.repository;
import com.example.clinical.domain.entity.AssessmentTaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface AssessmentTaskAssignmentRepository extends JpaRepository<AssessmentTaskAssignment, UUID> {}
