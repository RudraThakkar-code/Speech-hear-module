package com.example.clinical.repository;

import com.example.clinical.domain.entity.AssessmentTaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssessmentTaskAssignmentRepository extends JpaRepository<AssessmentTaskAssignment, UUID> {
    List<AssessmentTaskAssignment> findByEncounterId(UUID encounterId);
}
