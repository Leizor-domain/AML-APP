package com.leizo.admin.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
public class AuditLogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long alertId;
    private String username;
    private String action;
    private LocalDateTime timestamp;
    private String comment;

    // Getters and setters
    // ...
} 