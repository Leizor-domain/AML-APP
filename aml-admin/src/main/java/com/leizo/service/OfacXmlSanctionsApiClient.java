package com.leizo.service;

import com.leizo.model.SanctionedEntity;
import java.util.List;

/**
 * OFAC XML Sanctions API Client for real-time sanctions screening using the U.S. Treasury OFAC SDN list.
 * 
 * This service fetches and parses the official OFAC SDN XML feed from:
 * https://www.treasury.gov/ofac/downloads/sdn.xml
 * 
 * Provides methods for:
 * - Fetching and caching OFAC SDN data
 * - Matching transaction entities against sanctioned individuals/organizations
 * - Refreshing the sanctions list on demand
 * - Fuzzy name matching with configurable thresholds
 */
public interface OfacXmlSanctionsApiClient {
    
    /**
     * Checks if an entity is sanctioned by matching against the OFAC SDN list.
     * 
     * @param name The name to check (sender or receiver name)
     * @param country The country of the entity (optional, used for additional matching)
     * @return true if the entity matches a sanctioned individual/organization
     */
    boolean isEntitySanctioned(String name, String country);
    
    /**
     * Performs fuzzy name matching against the OFAC SDN list with configurable threshold.
     * 
     * @param name The name to check
     * @param threshold Similarity threshold (0.0 to 1.0, where 1.0 is exact match)
     * @return true if a match is found above the threshold
     */
    boolean isEntitySanctionedFuzzy(String name, double threshold);
    
    /**
     * Gets all cached sanctioned entities from the OFAC SDN list.
     * 
     * @return List of all sanctioned entities
     */
    List<SanctionedEntity> getAllSanctionedEntities();
    
    /**
     * Refreshes the OFAC SDN list by downloading the latest XML feed.
     * 
     * @return true if refresh was successful, false otherwise
     */
    boolean refreshSanctionsList();
    
    /**
     * Gets the last refresh timestamp of the sanctions list.
     * 
     * @return timestamp of last successful refresh
     */
    long getLastRefreshTimestamp();
    
    /**
     * Gets the total count of sanctioned entities currently cached.
     * 
     * @return number of sanctioned entities
     */
    int getSanctionedEntitiesCount();
    
    /**
     * Performs a detailed search and returns matching sanctioned entities.
     * 
     * @param name The name to search for
     * @param country Optional country filter
     * @return List of matching sanctioned entities with details
     */
    List<SanctionedEntity> searchSanctionedEntities(String name, String country);
} 