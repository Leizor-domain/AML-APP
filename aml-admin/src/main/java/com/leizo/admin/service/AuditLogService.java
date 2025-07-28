package com.leizo.admin.service;

import com.leizo.pojo.entity.AuditLogEntry;
import java.util.List;

public interface AuditLogService {
    void logAction(Long alertId, String username, String action, String comment);
    List<AuditLogEntry> getAuditTrail(Long alertId);
} 