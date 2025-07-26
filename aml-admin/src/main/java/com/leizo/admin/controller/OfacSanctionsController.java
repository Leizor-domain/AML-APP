package com.leizo.admin.controller;

import com.leizo.model.SanctionedEntity;
import com.leizo.service.OfacXmlSanctionsApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for OFAC sanctions screening operations.
 * 
 * Provides endpoints for:
 * - Testing sanctions screening against specific entities
 * - Monitoring OFAC data refresh status
 * - Searching sanctioned entities
 * - Manual refresh of OFAC data
 */
@RestController
@RequestMapping("/api/ofac")
public class OfacSanctionsController {

    private static final Logger logger = LoggerFactory.getLogger(OfacSanctionsController.class);

    private final OfacXmlSanctionsApiClient ofacSanctionsClient;

    public OfacSanctionsController(OfacXmlSanctionsApiClient ofacSanctionsClient) {
        this.ofacSanctionsClient = ofacSanctionsClient;
    }

    /**
     * Test if an entity is sanctioned.
     * 
     * @param name The name to check
     * @param country The country of the entity (optional)
     * @return JSON response with screening result
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkEntity(
            @RequestParam String name,
            @RequestParam(required = false) String country) {
        
        logger.info("OFAC screening request for entity: {} from country: {}", name, country);
        
        Map<String, Object> response = new HashMap<>();
        response.put("entity", name);
        response.put("country", country);
        response.put("isSanctioned", ofacSanctionsClient.isEntitySanctioned(name, country));
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Test fuzzy matching with configurable threshold.
     * 
     * @param name The name to check
     * @param threshold Similarity threshold (0.0 to 1.0)
     * @return JSON response with fuzzy matching result
     */
    @GetMapping("/check-fuzzy")
    public ResponseEntity<Map<String, Object>> checkEntityFuzzy(
            @RequestParam String name,
            @RequestParam(defaultValue = "0.8") double threshold) {
        
        logger.info("OFAC fuzzy screening request for entity: {} with threshold: {}", name, threshold);
        
        Map<String, Object> response = new HashMap<>();
        response.put("entity", name);
        response.put("threshold", threshold);
        response.put("isSanctioned", ofacSanctionsClient.isEntitySanctionedFuzzy(name, threshold));
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Search for sanctioned entities.
     * 
     * @param name Name to search for (optional)
     * @param country Country to filter by (optional)
     * @return List of matching sanctioned entities
     */
    @GetMapping("/search")
    public ResponseEntity<List<SanctionedEntity>> searchEntities(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String country) {
        
        logger.info("OFAC search request - name: {}, country: {}", name, country);
        
        List<SanctionedEntity> results = ofacSanctionsClient.searchSanctionedEntities(name, country);
        
        return ResponseEntity.ok(results);
    }

    /**
     * Get OFAC data statistics.
     * 
     * @return JSON response with OFAC data statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEntities", ofacSanctionsClient.getSanctionedEntitiesCount());
        stats.put("lastRefreshTimestamp", ofacSanctionsClient.getLastRefreshTimestamp());
        stats.put("lastRefreshDate", new java.util.Date(ofacSanctionsClient.getLastRefreshTimestamp()));
        stats.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Manually refresh OFAC data.
     * 
     * @return JSON response with refresh result
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshData() {
        
        logger.info("Manual OFAC data refresh requested");
        
        Map<String, Object> response = new HashMap<>();
        boolean success = ofacSanctionsClient.refreshSanctionsList();
        
        response.put("success", success);
        response.put("timestamp", System.currentTimeMillis());
        
        if (success) {
            response.put("message", "OFAC data refreshed successfully");
            response.put("entityCount", ofacSanctionsClient.getSanctionedEntitiesCount());
        } else {
            response.put("message", "Failed to refresh OFAC data");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all sanctioned entities (use with caution - may be large).
     * 
     * @return List of all sanctioned entities
     */
    @GetMapping("/entities")
    public ResponseEntity<List<SanctionedEntity>> getAllEntities() {
        
        logger.info("Request for all OFAC sanctioned entities");
        
        List<SanctionedEntity> entities = ofacSanctionsClient.getAllSanctionedEntities();
        
        return ResponseEntity.ok(entities);
    }

    /**
     * Health check endpoint for OFAC service.
     * 
     * @return JSON response with service health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        
        Map<String, Object> health = new HashMap<>();
        health.put("service", "OFAC Sanctions API Client");
        health.put("status", "UP");
        health.put("entityCount", ofacSanctionsClient.getSanctionedEntitiesCount());
        health.put("lastRefresh", ofacSanctionsClient.getLastRefreshTimestamp());
        health.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(health);
    }
} 