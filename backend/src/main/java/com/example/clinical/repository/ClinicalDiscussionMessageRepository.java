package com.example.clinical.repository;

import com.example.clinical.domain.entity.ClinicalDiscussionMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClinicalDiscussionMessageRepository extends JpaRepository<ClinicalDiscussionMessage, UUID> {
    List<ClinicalDiscussionMessage> findByDiscussion_IdOrderByCreatedAtAsc(UUID discussionId);
}
