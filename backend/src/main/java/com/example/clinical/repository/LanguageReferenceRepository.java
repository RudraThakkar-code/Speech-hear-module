package com.example.clinical.repository;

import com.example.clinical.domain.entity.LanguageReference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageReferenceRepository extends JpaRepository<LanguageReference, String> {
}
