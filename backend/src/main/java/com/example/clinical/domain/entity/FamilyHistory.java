package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.DataProvenance;
import com.example.clinical.domain.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "family_history")
@Getter
@Setter
@NoArgsConstructor
public class FamilyHistory extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ClinicalCase clinicalCase;

    @Column(name = "is_current", nullable = false)
    private boolean isCurrent = true;

    @Column(name = "family_speech_history", nullable = false)
    private boolean familySpeechHistory;

    @Column(name = "family_speech_details")
    private String familySpeechDetails;

    @Column(name = "family_hearing_history", nullable = false)
    private boolean familyHearingHistory;

    @Column(name = "family_hearing_details")
    private String familyHearingDetails;

    @Column(name = "other_relevant_family_hx")
    private String otherRelevantFamilyHx;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_provenance", columnDefinition = "data_provenance")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private DataProvenance dataProvenance;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", columnDefinition = "verification_status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private VerificationStatus verificationStatus;
}
