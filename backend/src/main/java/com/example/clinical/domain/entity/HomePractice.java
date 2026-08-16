package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.CompletionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "home_practice")
@Getter
@Setter
@NoArgsConstructor
public class HomePractice extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "practice_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private TherapySession session;

    @Column(name = "assigned_at", nullable = false)
    private ZonedDateTime assignedAt;

    @Column(name = "due_at", nullable = false)
    private ZonedDateTime dueAt;

    @Column(name = "completed_at")
    private ZonedDateTime completedAt;

    @Column(name = "task_description", nullable = false, columnDefinition = "TEXT")
    private String taskDescription;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "completion_status", nullable = false, columnDefinition = "completion_status")
    private CompletionStatus completionStatus;

    @Column(name = "practice_media_url")
    private String practiceMediaUrl;
}
