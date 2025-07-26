package com.leizo.service.impl;

import com.leizo.model.SanctionedEntity;
import com.leizo.service.OfacXmlSanctionsApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import jakarta.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of OFAC XML Sanctions API Client that fetches and parses the official OFAC SDN list.
 * 
 * Features:
 * - Real-time XML parsing from U.S. Treasury OFAC feed
 * - In-memory caching with periodic refresh
 * - Fuzzy name matching using Levenshtein distance
 * - Case-insensitive and whitespace-tolerant matching
 * - Comprehensive error handling and fallback mechanisms
 */
@Service
public class OfacXmlSanctionsApiClientImpl implements OfacXmlSanctionsApiClient {
    
    private static final Logger logger = LoggerFactory.getLogger(OfacXmlSanctionsApiClientImpl.class);
    
    // OFAC SDN XML feed URL
    private static final String OFAC_SDN_URL = "https://www.treasury.gov/ofac/downloads/sdn.xml";
    
    // XML element names in the OFAC SDN feed
    private static final String SDN_ENTRY_TAG = "sdnEntry";
    private static final String LAST_NAME_TAG = "lastName";
    private static final String FIRST_NAME_TAG = "firstName";
    private static final String SDN_TYPE_TAG = "sdnType";
    private static final String REMARKS_TAG = "remarks";
    private static final String PROGRAM_LIST_TAG = "programList";
    private static final String PROGRAM_TAG = "program";
    private static final String ID_LIST_TAG = "idList";
    private static final String ID_TAG = "id";
    private static final String ID_TYPE_TAG = "idType";
    private static final String ID_NUMBER_TAG = "idNumber";
    private static final String ID_COUNTRY_TAG = "idCountry";
    
    // Caching and refresh settings
    private static final long REFRESH_INTERVAL_HOURS = 24; // Refresh every 24 hours
    private static final double DEFAULT_FUZZY_THRESHOLD = 0.8; // 80% similarity threshold
    
    private final RestTemplate restTemplate;
    private final ScheduledExecutorService scheduler;
    
    // Cached data
    private final List<SanctionedEntity> sanctionedEntities = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, SanctionedEntity> nameToEntityMap = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> countryToNamesMap = new ConcurrentHashMap<>();
    
    private long lastRefreshTimestamp = 0;
    private boolean isInitialized = false;
    
    // Default constructor for production
    public OfacXmlSanctionsApiClientImpl() {
        this(new RestTemplate());
    }
    // Constructor for test injection
    public OfacXmlSanctionsApiClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }
    
    @PostConstruct
    public void initialize() {
        logger.info("Initializing OFAC XML Sanctions API Client...");
        
        // Load initial sanctions list
        if (refreshSanctionsList()) {
            isInitialized = true;
            logger.info("OFAC XML Sanctions API Client initialized successfully with {} entities", 
                       sanctionedEntities.size());
            
            // Schedule periodic refresh
            scheduler.scheduleAtFixedRate(
                this::refreshSanctionsList,
                REFRESH_INTERVAL_HOURS,
                REFRESH_INTERVAL_HOURS,
                TimeUnit.HOURS
            );
        } else {
            logger.error("Failed to initialize OFAC XML Sanctions API Client");
        }
    }
    
    @Override
    public boolean isEntitySanctioned(String name, String country) {
        if (!isInitialized || name == null || name.trim().isEmpty()) {
            return false;
        }
        
        String normalizedName = normalizeName(name);
        
        // First, try exact match
        if (nameToEntityMap.containsKey(normalizedName)) {
            logger.warn("OFAC EXACT MATCH: Entity [{}] from [{}] is sanctioned!", name, country);
            return true;
        }
        
        // Substring/partial match
        for (String storedName : nameToEntityMap.keySet()) {
            if (storedName.contains(normalizedName) || normalizedName.contains(storedName)) {
                logger.warn("OFAC PARTIAL MATCH: [{}] ~ [{}]", normalizedName, storedName);
                return true;
            }
        }
        
        // Then, try fuzzy matching
        for (SanctionedEntity entity : sanctionedEntities) {
            String entityName = normalizeName(entity.getName());
            double similarity = calculateSimilarity(normalizedName, entityName);
            if (similarity >= DEFAULT_FUZZY_THRESHOLD) {
                logger.warn("OFAC FUZZY MATCH: Entity [{}] from [{}] is potentially sanctioned!", name, country);
                return true;
            }
        }
        
        // Finally, check country-based matching if country is provided
        if (country != null && !country.trim().isEmpty()) {
            String normalizedCountry = normalizeCountry(country);
            Set<String> countryNames = countryToNamesMap.get(normalizedCountry);
            if (countryNames != null) {
                for (String storedName : countryNames) {
                    if (storedName.contains(normalizedName) || normalizedName.contains(storedName)) {
                        logger.warn("OFAC COUNTRY PARTIAL MATCH: [{}] ~ [{}] in [{}]", normalizedName, storedName, normalizedCountry);
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    @Override
    public boolean isEntitySanctionedFuzzy(String name, double threshold) {
        if (!isInitialized || name == null || name.trim().isEmpty()) {
            return false;
        }
        
        String normalizedName = normalizeName(name);
        logger.debug("Fuzzy checking normalized name: [{}]", normalizedName);
        
        for (SanctionedEntity entity : sanctionedEntities) {
            String entityName = normalizeName(entity.getName());
            double similarity = calculateSimilarity(normalizedName, entityName);
            
            if (similarity >= threshold) {
                logger.debug("OFAC FUZZY MATCH: [{}] matches [{}] with similarity {:.2f}", 
                           normalizedName, entityName, similarity);
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public List<SanctionedEntity> getAllSanctionedEntities() {
        return new ArrayList<>(sanctionedEntities);
    }
    
    @Override
    public boolean refreshSanctionsList() {
        try {
            logger.info("Refreshing OFAC SDN list from {}", OFAC_SDN_URL);
            
            // Fetch XML data
            ResponseEntity<String> response = restTemplate.getForEntity(OFAC_SDN_URL, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Parse XML and update cache
                List<SanctionedEntity> newEntities = parseOfacXml(response.getBody());
                
                synchronized (sanctionedEntities) {
                    sanctionedEntities.clear();
                    sanctionedEntities.addAll(newEntities);
                    
                    // Update maps
                    updateMaps(newEntities);
                }
                
                lastRefreshTimestamp = System.currentTimeMillis();
                logger.info("Successfully refreshed OFAC SDN list with {} entities", newEntities.size());
                return true;
                
            } else {
                logger.error("Failed to fetch OFAC SDN list: HTTP {}", response.getStatusCode());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Error refreshing OFAC SDN list: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public long getLastRefreshTimestamp() {
        return lastRefreshTimestamp;
    }
    
    @Override
    public int getSanctionedEntitiesCount() {
        return sanctionedEntities.size();
    }
    
    @Override
    public List<SanctionedEntity> searchSanctionedEntities(String name, String country) {
        List<SanctionedEntity> results = new ArrayList<>();
        
        if (!isInitialized) {
            return results;
        }
        
        String normalizedName = name != null ? normalizeName(name) : "";
        String normalizedCountry = country != null ? normalizeCountry(country) : "";
        logger.debug("Searching for normalized name: [{}], country: [{}]", normalizedName, normalizedCountry);
        
        for (SanctionedEntity entity : sanctionedEntities) {
            String entityName = normalizeName(entity.getName());
            boolean nameMatches = normalizedName.isEmpty() || 
                                entityName.contains(normalizedName) || normalizedName.contains(entityName);
            boolean countryMatches = normalizedCountry.isEmpty() || 
                                   normalizeCountry(entity.getCountry()).contains(normalizedCountry);
            
            if (nameMatches && countryMatches) {
                results.add(entity);
            }
        }
        
        return results;
    }
    
    /**
     * Parses the OFAC SDN XML feed and extracts sanctioned entities.
     */
    private List<SanctionedEntity> parseOfacXml(String xmlContent) {
        List<SanctionedEntity> entities = new ArrayList<>();
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            try (InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes("UTF-8"))) {
                Document document = builder.parse(inputStream);
                document.getDocumentElement().normalize();
                
                NodeList sdnEntries = document.getElementsByTagName(SDN_ENTRY_TAG);
                
                for (int i = 0; i < sdnEntries.getLength(); i++) {
                    Element sdnEntry = (Element) sdnEntries.item(i);
                    SanctionedEntity entity = parseSdnEntry(sdnEntry);
                    if (entity != null) {
                        entities.add(entity);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error parsing OFAC XML: {}", e.getMessage(), e);
        }
        
        return entities;
    }
    
    /**
     * Parses a single SDN entry from the XML.
     */
    private SanctionedEntity parseSdnEntry(Element sdnEntry) {
        try {
            // Extract basic information
            String lastName = getElementText(sdnEntry, LAST_NAME_TAG);
            String firstName = getElementText(sdnEntry, FIRST_NAME_TAG);
            String sdnType = getElementText(sdnEntry, SDN_TYPE_TAG);
            String remarks = getElementText(sdnEntry, REMARKS_TAG);
            
            // Build full name
            String fullName = buildFullName(firstName, lastName);
            if (fullName.trim().isEmpty()) {
                return null; // Skip entries without names
            }
            
            // Extract country from ID list
            String country = extractCountryFromIdList(sdnEntry);
            
            // Extract program information
            String program = extractProgram(sdnEntry);
            
            // Create SanctionedEntity
            return new SanctionedEntity(
                fullName,
                country != null ? country : "Unknown",
                null, // DOB not available in OFAC SDN
                "OFAC"
            );
            
        } catch (Exception e) {
            logger.warn("Error parsing SDN entry: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Extracts country information from the ID list.
     */
    private String extractCountryFromIdList(Element sdnEntry) {
        Element idList = getChildElement(sdnEntry, ID_LIST_TAG);
        if (idList != null) {
            NodeList ids = idList.getElementsByTagName(ID_TAG);
            for (int i = 0; i < ids.getLength(); i++) {
                Element id = (Element) ids.item(i);
                String idCountry = getElementText(id, ID_COUNTRY_TAG);
                if (idCountry != null && !idCountry.trim().isEmpty()) {
                    return idCountry.trim();
                }
            }
        }
        return null;
    }
    
    /**
     * Extracts program information.
     */
    private String extractProgram(Element sdnEntry) {
        Element programList = getChildElement(sdnEntry, PROGRAM_LIST_TAG);
        if (programList != null) {
            NodeList programs = programList.getElementsByTagName(PROGRAM_TAG);
            if (programs.getLength() > 0) {
                return programs.item(0).getTextContent().trim();
            }
        }
        return null;
    }
    
    /**
     * Builds full name from first and last name.
     */
    private String buildFullName(String firstName, String lastName) {
        StringBuilder fullName = new StringBuilder();
        
        if (firstName != null && !firstName.trim().isEmpty()) {
            fullName.append(firstName.trim());
        }
        
        if (lastName != null && !lastName.trim().isEmpty()) {
            if (fullName.length() > 0) {
                fullName.append(" ");
            }
            fullName.append(lastName.trim());
        }
        
        return fullName.toString();
    }
    
    /**
     * Gets text content of a child element.
     */
    private String getElementText(Element parent, String tagName) {
        Element element = getChildElement(parent, tagName);
        return element != null ? element.getTextContent().trim() : null;
    }
    
    /**
     * Gets a child element by tag name.
     */
    private Element getChildElement(Element parent, String tagName) {
        NodeList children = parent.getElementsByTagName(tagName);
        return children.getLength() > 0 ? (Element) children.item(0) : null;
    }
    
    /**
     * Updates the name and country maps for efficient lookups.
     */
    private void updateMaps(List<SanctionedEntity> entities) {
        nameToEntityMap.clear();
        countryToNamesMap.clear();
        
        for (SanctionedEntity entity : entities) {
            String normalizedName = normalizeName(entity.getName());
            String[] nameParts = normalizedName.split(" ");
            String reversedName = normalizedName;
            if (nameParts.length == 2) {
                reversedName = nameParts[1] + " " + nameParts[0];
            }
            // Update name map for both orders
            nameToEntityMap.put(normalizedName, entity);
            if (!reversedName.equals(normalizedName)) {
                nameToEntityMap.put(reversedName, entity);
            }
            // Update country map for both orders
            String normalizedCountry = normalizeCountry(entity.getCountry());
            countryToNamesMap.computeIfAbsent(normalizedCountry, k -> new HashSet<>()).add(normalizedName);
            if (!reversedName.equals(normalizedName)) {
                countryToNamesMap.get(normalizedCountry).add(reversedName);
            }
        }
    }
    
    /**
     * Normalizes a name for consistent matching.
     */
    private String normalizeName(String name) {
        if (name == null) return "";
        return name.toLowerCase().trim().replaceAll("\\s+", " ");
    }
    
    /**
     * Normalizes a country name for consistent matching.
     */
    private String normalizeCountry(String country) {
        if (country == null) return "";
        return country.toLowerCase().trim().replaceAll("\\s+", " ");
    }
    
    /**
     * Calculates similarity between two strings using Levenshtein distance.
     */
    private double calculateSimilarity(String s1, String s2) {
        if (s1.equals(s2)) return 1.0;
        if (s1.isEmpty() || s2.isEmpty()) return 0.0;
        
        int distance = levenshteinDistance(s1, s2);
        int maxLength = Math.max(s1.length(), s2.length());
        
        return 1.0 - ((double) distance / maxLength);
    }
    
    /**
     * Calculates Levenshtein distance between two strings.
     */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], 
                                          Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
} 