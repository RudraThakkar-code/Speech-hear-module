package com.example.clinical.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "language_reference")
@Getter
@Setter
@NoArgsConstructor
public class LanguageReference {

    @Id
    @Column(name = "language_code", length = 10, nullable = false)
    private String languageCode;

    @Column(name = "language_name", nullable = false)
    private String languageName;
}
