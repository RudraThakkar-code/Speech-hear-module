package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.DataProvenance;
import com.example.clinical.domain.enums.DeliveryType;
import com.example.clinical.domain.enums.PrematurityStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;


import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "birth_history")
@Getter
@Setter
@NoArgsConstructor
public class BirthHistory extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ClinicalCase clinicalCase;

    @Column(name = "is_current", nullable = false)
    private boolean isCurrent = true;

    @Column(name = "pregnancy_complications", nullable = false)
    private boolean pregnancyComplications;

    @Column(name = "pregnancy_complication_details")
    private String pregnancyComplicationDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type", nullable = false, columnDefinition = "delivery_type")
    @JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    private DeliveryType deliveryType;

    @Enumerated(EnumType.STRING)
    @Column(name = "prematurity_status", nullable = false, columnDefinition = "prematurity_status")
    @JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    private PrematurityStatus prematurityStatus;

    @Column(name = "birth_weight")
    private BigDecimal birthWeight;

    @Column(name = "nicu_admission", nullable = false)
    private boolean nicuAdmission;

    @Column(name = "other_relevant_birth_hx")
    private String otherRelevantBirthHx;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_provenance", columnDefinition = "data_provenance")
    @JdbcTypeCode(org.hibernate.type.SqlTypes.NAMED_ENUM)
    private DataProvenance dataProvenance;
}
