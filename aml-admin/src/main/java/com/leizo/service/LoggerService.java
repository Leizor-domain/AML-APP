package com.leizo.service;

public interface LoggerService {

    void logRiskScore(String sender, int score);

    void logEvent(String eventType, String actor, String details);

    void logAlert(com.leizo.admin.entity.Alert alert);

}
