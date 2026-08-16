package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.TaskType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "assessment_task_library")
@Getter
@Setter
@NoArgsConstructor
public class AssessmentTaskLibrary {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "task_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "task_version", nullable = false)
    private Integer taskVersion;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, columnDefinition = "task_type")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TaskType taskType;

    @Column(name = "instructions", nullable = false)
    private String instructions;

    @Column(name = "prompt_material", nullable = false)
    private String promptMaterial;
}
