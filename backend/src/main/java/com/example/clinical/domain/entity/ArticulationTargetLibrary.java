package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.TargetPosition;
import com.example.clinical.domain.enums.TargetType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "articulation_target_library")
@Getter
@Setter
@NoArgsConstructor
public class ArticulationTargetLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "target_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "target_version", nullable = false)
    private Integer targetVersion;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_code", nullable = false)
    private LanguageReference language;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TargetType targetType;

    @Column(name = "phoneme", nullable = false)
    private String phoneme;

    @Column(name = "word")
    private String word;

    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TargetPosition position;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "retired_at")
    private ZonedDateTime retiredAt;
}
