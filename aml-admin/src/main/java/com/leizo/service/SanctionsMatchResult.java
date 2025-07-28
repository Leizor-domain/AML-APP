package com.leizo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Result of sanctions screening operation
 * Contains detailed information about any sanctions matches found
 */
public class SanctionsMatchResult {
    
    private final boolean isSanctioned;
    private final String matchedEntityName;
    private final String matchedCountry;
    private final String matchedList;
    private final String matchReason;
    private final String sanctioningBody;
    private final LocalDateTime matchTimestamp;
    private final Map<String, Object> additionalDetails;
    private final List<String> matchedFields;
    private final double confidenceScore;
    
    public SanctionsMatchResult(boolean isSanctioned, String matchedEntityName, 
                              String matchedCountry, String matchedList, 
                              String matchReason, String sanctioningBody,
                              LocalDateTime matchTimestamp, 
                              Map<String, Object> additionalDetails,
                              List<String> matchedFields, double confidenceScore) {
        this.isSanctioned = isSanctioned;
        this.matchedEntityName = matchedEntityName;
        this.matchedCountry = matchedCountry;
        this.matchedList = matchedList;
        this.matchReason = matchReason;
        this.sanctioningBody = sanctioningBody;
        this.matchTimestamp = matchTimestamp;
        this.additionalDetails = additionalDetails;
        this.matchedFields = matchedFields;
        this.confidenceScore = confidenceScore;
    }
    
    // Static factory methods for common scenarios
    public static SanctionsMatchResult noMatch() {
        return new SanctionsMatchResult(false, null, null, null, null, null, 
                LocalDateTime.now(), Map.of(), List.of(), 0.0);
    }
    
    public static SanctionsMatchResult ofacMatch(String entityName, String country, 
                                               String reason, double confidence) {
        return new SanctionsMatchResult(true, entityName, country, "OFAC_SDN", 
                reason, "OFAC", LocalDateTime.now(), Map.of(), 
                List.of("name", "country"), confidence);
    }
    
    public static SanctionsMatchResult localMatch(String entityName, String country, 
                                                String reason, double confidence) {
        return new SanctionsMatchResult(true, entityName, country, "LOCAL_SANCTIONS", 
                reason, "LOCAL", LocalDateTime.now(), Map.of(), 
                List.of("name", "country"), confidence);
    }
    
    public static SanctionsMatchResult countryMatch(String country, String reason) {
        return new SanctionsMatchResult(true, null, country, "COUNTRY_SANCTIONS", 
                reason, "UN", LocalDateTime.now(), Map.of(), 
                List.of("country"), 1.0);
    }
    
    // Getters
    public boolean isSanctioned() { return isSanctioned; }
    public String getMatchedEntityName() { return matchedEntityName; }
    public String getMatchedCountry() { return matchedCountry; }
    public String getMatchedList() { return matchedList; }
    public String getMatchReason() { return matchReason; }
    public String getSanctioningBody() { return sanctioningBody; }
    public LocalDateTime getMatchTimestamp() { return matchTimestamp; }
    public Map<String, Object> getAdditionalDetails() { return additionalDetails; }
    public List<String> getMatchedFields() { return matchedFields; }
    public double getConfidenceScore() { return confidenceScore; }
    
    /**
     * Gets a formatted reason string for alert creation
     */
    public String getFormattedReason() {
        if (!isSanctioned) {
            return "No sanctions match found";
        }
        
        StringBuilder reason = new StringBuilder();
        if (matchedEntityName != null) {
            reason.append("Entity '").append(matchedEntityName).append("' ");
        }
        if (matchedCountry != null) {
            reason.append("from '").append(matchedCountry).append("' ");
        }
        reason.append("matched in ").append(matchedList).append(" list");
        
        if (matchReason != null) {
            reason.append(": ").append(matchReason);
        }
        
        return reason.toString();
    }
    
    @Override
    public String toString() {
        return "SanctionsMatchResult{" +
                "isSanctioned=" + isSanctioned +
                ", matchedEntityName='" + matchedEntityName + '\'' +
                ", matchedCountry='" + matchedCountry + '\'' +
                ", matchedList='" + matchedList + '\'' +
                ", matchReason='" + matchReason + '\'' +
                ", confidenceScore=" + confidenceScore +
                '}';
    }
} 