package com.example.clinical.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "follow_up")
@Getter
@Setter
@NoArgsConstructor
public class FollowUp extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "followup_id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "encounter_id", nullable = false, unique = true)
    private Encounter encounter;

    @Column(name = "previous_recommendations_met", nullable = false)
    private boolean previousRecommendationsMet;

    @Column(name = "current_status_summary", nullable = false, columnDefinition = "TEXT")
    private String currentStatusSummary;
}
