package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.ClinicalDiscussionPriority;
import com.example.clinical.domain.enums.ClinicalDiscussionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "clinical_discussion")
@Getter
@Setter
@NoArgsConstructor
public class ClinicalDiscussion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "discussion_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private ClinicalCase clinicalCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id")
    private Encounter encounter;

    @Column(name = "topic", nullable = false, columnDefinition = "TEXT")
    private String topic;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false, columnDefinition = "clinical_discussion_status")
    private ClinicalDiscussionStatus status;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "priority", nullable = false, columnDefinition = "clinical_discussion_priority")
    private ClinicalDiscussionPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdByUser;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
}
