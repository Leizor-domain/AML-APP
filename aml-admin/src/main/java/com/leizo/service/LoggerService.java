package com.leizo.service;

import com.leizo.model.Alert;

public interface LoggerService {

    void logRiskScore(String sender, int score);

    void logEvent(String eventType, String actor, String details);

    void logAlert(Alert alert);

}
