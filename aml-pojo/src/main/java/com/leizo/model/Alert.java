package com.leizo.model;

public class Alert {

    private String alertId;
    private Transaction transaction;
    private Rule rule;
    private String reason;
    private long timestamp;
    private String alertType;
    private String priorityLevel;
    private String fingerPrint;
    private int priorityScore;

    // Generate or add this setter
    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    // Also add this getter if missing
    public String getAlertId() {
        return alertId;
    }

    // Other setters and getters
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public Rule getRule() {
        return rule;
    }

    public String getReason() {
        return reason;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }

    public String getFingerPrint() {
        return fingerPrint;
    }
    public void setPriorityScore(int priorityScore) {
        this.priorityScore = priorityScore;
    }
    public int getPriorityScore() {
        return priorityScore;
    }

    public void updatePriorityLevel() {
        if(priorityScore >= 75) {
            this.priorityLevel = "High";
        } else if(priorityScore >= 40) {
            this.priorityLevel = "Medium";
        } else {
            this.priorityLevel = "Low";
        }
    }

    @Override
    public String toString() {
        return "Alert{" +
                "alertId='" + alertId + '\'' +
                ", reason='" + reason + '\'' +
                ", alertType='" + alertType + '\'' +
                ", priorityLevel='" + priorityLevel + '\'' +
                ", transaction=" + (transaction != null ? transaction.toString() : "null") +
                ", timestamp=" + timestamp +
                ", fingerPrint='" + fingerPrint + '\'' +
                '}';
    }


}
