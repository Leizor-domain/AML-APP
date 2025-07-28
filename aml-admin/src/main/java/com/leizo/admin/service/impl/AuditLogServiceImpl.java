package com.leizo.admin.service.impl;

import com.leizo.pojo.entity.AuditLogEntry;
import com.leizo.admin.repository.AuditLogRepository;
import com.leizo.admin.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {
    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public void logAction(Long alertId, String username, String action, String comment) {
        AuditLogEntry entry = new AuditLogEntry();
        entry.setAlertId(alertId);
        entry.setUsername(username);
        entry.setAction(action);
        entry.setTimestamp(LocalDateTime.now());
        entry.setComment(comment);
        auditLogRepository.save(entry);
    }

    @Override
    public List<AuditLogEntry> getAuditTrail(Long alertId) {
        return auditLogRepository.findByAlertIdOrderByTimestampAsc(alertId);
    }
} 