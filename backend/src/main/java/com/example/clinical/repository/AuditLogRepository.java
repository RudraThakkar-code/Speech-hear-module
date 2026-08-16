package com.example.clinical.repository;

import com.example.clinical.domain.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByTableNameAndRecordIdOrderByOccurredAtDesc(String tableName, UUID recordId);
}
