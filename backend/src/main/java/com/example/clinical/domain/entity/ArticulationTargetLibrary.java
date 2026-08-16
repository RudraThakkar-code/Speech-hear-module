package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.TargetPosition;
import com.example.clinical.domain.enums.TargetType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;

@Entity
@Table(name = "articulation_target_library")
@Getter @Setter @NoArgsConstructor
public class ArticulationTargetLibrary {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "target_id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "target_version", nullable = false) private Integer targetVersion;
    @Column(name = "active", nullable = false) private boolean active;
    @Column(name = "language_code", nullable = false) private String languageCode;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "target_type", nullable = false, columnDefinition = "target_type") private TargetType targetType;
    @Column(name = "phoneme", nullable = false) private String phoneme;
    @Column(name = "word") private String word;
    @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "position", columnDefinition = "target_position") private TargetPosition position;
}
