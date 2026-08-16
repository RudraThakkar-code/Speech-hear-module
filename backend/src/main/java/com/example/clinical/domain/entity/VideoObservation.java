package com.example.clinical.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "video_observation")
@Getter
@Setter
@NoArgsConstructor
public class VideoObservation extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "video_observation_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "encounter_id", nullable = false)
    private Encounter encounter;

    @Column(name = "video_sample_url", nullable = false)
    private String videoSampleUrl;

    @Column(name = "camera_position", nullable = false, columnDefinition = "camera_position")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private String cameraPosition;

    @Column(name = "video_context", nullable = false, columnDefinition = "TEXT")
    private String videoContext;

    @Column(name = "secondary_behavior_eye", columnDefinition = "TEXT")
    private String secondaryBehaviorEye;

    @Column(name = "secondary_behavior_facial", columnDefinition = "TEXT")
    private String secondaryBehaviorFacial;

    @Column(name = "secondary_behavior_limb", columnDefinition = "TEXT")
    private String secondaryBehaviorLimb;
}
