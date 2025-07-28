package com.leizo.admin.repository;

import com.leizo.pojo.entity.AuditLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntry, Long> {
    List<AuditLogEntry> findByAlertIdOrderByTimestampAsc(Long alertId);
} 