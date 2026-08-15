package com.example.clinical.domain.entity;

import com.example.clinical.domain.enums.DataProvenance;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "chief_complaint")
@Getter
@Setter
@NoArgsConstructor
public class ChiefComplaint extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ClinicalCase clinicalCase;

    @Column(name = "is_current", nullable = false)
    private boolean isCurrent = true;

    @Column(name = "complaint_text", nullable = false)
    private String complaintText;

    @Column(name = "complaint_audio_url")
    private String complaintAudioUrl;

    @Column(name = "onset_duration", nullable = false)
    private String onsetDuration;

    @Column(name = "previous_consultation", nullable = false)
    private boolean previousConsultation;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_provenance", columnDefinition = "data_provenance")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private DataProvenance dataProvenance;
}
