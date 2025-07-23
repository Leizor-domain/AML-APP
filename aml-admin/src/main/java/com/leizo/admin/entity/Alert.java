package com.leizo.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "alert_id", nullable = false, unique = true, length = 100)
    private String alertId;

    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "alert_type", length = 50)
    private String alertType;

    @Column(name = "priority_level", length = 20)
    private String priorityLevel;

    private String matchedEntityName;
    private String matchedList;
    private String matchReason;

    @Transient
    private Transaction transaction;

    @Column(name = "priority_score")
    private Integer priorityScore;

    // Constructors
    public Alert() {}

    public Alert(String alertId, Integer transactionId, String reason, LocalDateTime timestamp) {
        this.alertId = alertId;
        this.transactionId = transactionId;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getMatchedEntityName() { return matchedEntityName; }
    public void setMatchedEntityName(String matchedEntityName) { this.matchedEntityName = matchedEntityName; }
    public String getMatchedList() { return matchedList; }
    public void setMatchedList(String matchedList) { this.matchedList = matchedList; }
    public String getMatchReason() { return matchReason; }
    public void setMatchReason(String matchReason) { this.matchReason = matchReason; }

    public Transaction getTransaction() {
        return transaction;
    }
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
        if (transaction != null) {
            this.transactionId = transaction.getId();
        }
    }
    public Integer getPriorityScore() {
        return priorityScore;
    }
    public void setPriorityScore(Integer priorityScore) {
        this.priorityScore = priorityScore;
    }
    public void updatePriorityLevel() {
        if (priorityScore != null) {
            if (priorityScore >= 80) this.priorityLevel = "HIGH";
            else if (priorityScore >= 50) this.priorityLevel = "MEDIUM";
            else this.priorityLevel = "LOW";
        }
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", alertId='" + alertId + '\'' +
                ", reason='" + reason + '\'' +
                ", alertType='" + alertType + '\'' +
                ", priorityLevel='" + priorityLevel + '\'' +
                ", transactionId=" + transactionId +
                ", timestamp=" + timestamp +
                '}';
    }
} 