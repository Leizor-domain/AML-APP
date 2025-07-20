package com.leizo.model;

import com.leizo.enums.IngestionStatus;

public class IngestionResult {

    private final IngestionStatus status;
    private final boolean alertGenerated;
    private final String alertId;
    private final int riskScore;

    public IngestionResult(IngestionStatus status, boolean alertGenerated, String alertId, int riskScore) {
        this.status = status;
        this.alertGenerated = alertGenerated;
        this.alertId = alertId;
        this.riskScore = riskScore;

    }

    public IngestionStatus getStatus(){
        return status;
    }

    public boolean isAlertGenerated(){
        return alertGenerated;
    }

    public String getAlertId(){
        return alertId;
    }
    public int getRiskScore(){
        return riskScore;
    }

    @Override
    public String toString(){
        return "IngestionResult{" +
                "Status=" + status +
                ", alertGenerated=" + alertGenerated +
                ", alertId='" + alertId + ", riskScore=" + riskScore + '\'' +
                '}';
    }


}
