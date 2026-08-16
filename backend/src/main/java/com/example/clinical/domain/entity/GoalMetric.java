package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.MetricType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "goal_metric")
@Getter
@Setter
@NoArgsConstructor
public class GoalMetric extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "metric_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "goal_id", nullable = false)
    private TherapyGoal goal;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "metric_type", nullable = false, columnDefinition = "metric_type")
    private MetricType metricType;

    @Column(name = "metric_name", nullable = false)
    private String metricName;

    @Column(name = "value", nullable = false)
    private BigDecimal value;

    @Column(name = "unit", nullable = false)
    private String unit;
}
