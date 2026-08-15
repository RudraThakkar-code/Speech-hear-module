package com.example.clinical.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "language_history")
@Getter
@Setter
@NoArgsConstructor
public class LanguageHistory extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ClinicalCase clinicalCase;

    @Column(name = "is_current", nullable = false)
    private boolean isCurrent = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_spoken_by_child", referencedColumnName = "language_code", nullable = false)
    private LanguageReference languageSpokenByChild;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "language_history_understood",
        joinColumns = @JoinColumn(name = "history_id"),
        inverseJoinColumns = @JoinColumn(name = "language_code")
    )
    private Set<LanguageReference> languagesUnderstood = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "language_history_heard_at_home",
        joinColumns = @JoinColumn(name = "history_id"),
        inverseJoinColumns = @JoinColumn(name = "language_code")
    )
    private Set<LanguageReference> languagesHeardAtHome = new HashSet<>();

    @Column(name = "language_exposure_details")
    private String languageExposureDetails;

    @Column(name = "first_words_age")
    private Integer firstWordsAge;

    @Column(name = "phrase_speech_age")
    private Integer phraseSpeechAge;

    @Column(name = "language_development_concerns")
    private String languageDevelopmentConcerns;
}
