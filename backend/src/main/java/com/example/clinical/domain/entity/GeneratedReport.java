package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.ReportType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "generated_report")
@Getter
@Setter
@NoArgsConstructor
public class GeneratedReport {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "generated_report_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private ClinicalCase clinicalCase;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "report_type", nullable = false, columnDefinition = "report_type")
    private ReportType reportType;

    @Column(name = "report_content", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Object reportContent;

    @Column(name = "generated_at", nullable = false)
    private ZonedDateTime generatedAt;
}
