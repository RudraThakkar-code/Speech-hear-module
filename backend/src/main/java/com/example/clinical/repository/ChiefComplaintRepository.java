package com.example.clinical.repository;

import com.example.clinical.domain.entity.ChiefComplaint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ChiefComplaintRepository extends JpaRepository<ChiefComplaint, UUID> {
}
