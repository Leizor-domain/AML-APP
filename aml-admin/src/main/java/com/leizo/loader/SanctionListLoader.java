package com.leizo.loader;

import com.leizo.pojo.entity.SanctionedEntity;
import com.leizo.service.FileImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * SanctionListLoader is responsible for loading and centralizing sanctioned entities
 * from various data sources (e.g., OFAC, UN, EU, UK) and maintaining
 * high-risk country information. It acts as a gatekeeper for sanction screening.
 */
@Component
public class SanctionListLoader {

    private final FileImportService fileImportService;
    private final List<SanctionedEntity> consolidatedList;
    private final Set<String> highRiskCountries;

    /**
     * Initializes the loader with a given import service. If none is provided,
     * it defaults to the FileImportServiceImpl.
     *
     * All sanctioned data and high-risk countries are loaded on construction.
     *
     * @param fileImportService file import abstraction
     */
    @Autowired
    public SanctionListLoader(FileImportService fileImportService) {
        this.fileImportService = fileImportService;
        this.consolidatedList = new ArrayList<>();
        this.highRiskCountries = new HashSet<>();
        loadAllLists();
    }

    /**
     * Loads all sanctioned entity sources and high-risk countries into memory.
     * This method can be easily adapted to support more sources or formats.
     */
    private void loadAllLists() {
        try {
            // Load sample sanctions data for testing
            String sampleSanctionsPath = "src/main/resources/data/sample_sanctions.csv";
            List<SanctionedEntity> sampleSanctions = fileImportService.importCsv(sampleSanctionsPath);
            consolidatedList.addAll(sampleSanctions);
            
            // Add some high-risk countries for testing
            highRiskCountries.add("Iran");
            highRiskCountries.add("North Korea");
            highRiskCountries.add("Syria");
            highRiskCountries.add("Venezuela");
            highRiskCountries.add("Cuba");
            highRiskCountries.add("Russia");
            
            System.out.println("[SanctionListLoader] Loaded " + consolidatedList.size() + " sanctioned entities");
            System.out.println("[SanctionListLoader] Loaded " + highRiskCountries.size() + " high-risk countries");
            
        } catch (Exception e) {
            System.err.println("[SanctionListLoader] Failed to load lists: " + e.getMessage());
            // Load minimal test data if file loading fails
            loadMinimalTestData();
        }
    }

    /**
     * Loads minimal test data if file loading fails
     */
    private void loadMinimalTestData() {
        // Add some test sanctioned entities
        consolidatedList.add(new SanctionedEntity("John Smith", "USA", "1980-01-15", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Maria Garcia", "Spain", "1975-03-22", "EU"));
        consolidatedList.add(new SanctionedEntity("Ahmed Hassan", "Egypt", "1985-07-10", "UN"));
        
        // Add some high-risk countries
        highRiskCountries.add("Iran");
        highRiskCountries.add("North Korea");
        highRiskCountries.add("Syria");
        
        System.out.println("[SanctionListLoader] Loaded minimal test data: " + consolidatedList.size() + " entities");
    }

    /**
     * Retrieves an unmodifiable list of all loaded sanctioned entities.
     *
     * @return list of sanctioned entities
     */
    public List<SanctionedEntity> getConsolidatedList(){
        return Collections.unmodifiableList(consolidatedList);
    }

    /**
     * Returns a read-only set of all loaded high-risk countries.
     *
     * @return set of high-risk country names
     */
    public Set<String> getHighRiskCountries() {
        return Collections.unmodifiableSet(highRiskCountries);
    }

    //Checks if a full entity (name, country, dob, sanctioning body) exists in the sanctions list.
    public boolean isEntitySanctioned(String name, String country, String dob, String sanctioningBody) {
        return consolidatedList.stream().anyMatch(e ->
                e.getName().equalsIgnoreCase(name) &&
                e.getCountry().equalsIgnoreCase(country) &&
                e.getDob().equalsIgnoreCase(dob) &&
                e.getSanctioningBody().equalsIgnoreCase(sanctioningBody)
        );
    }

    // Checks if a name exists (exact match) in any sanctioned record.
    public boolean isNameSanctioned(String name) {
        return consolidatedList.stream().anyMatch(e ->
                e.getName().equalsIgnoreCase(name)
        );
    }

    // Check if a country is high risk based on entities
    public boolean isCountrySanctioned(String country) {
        return consolidatedList.stream().anyMatch(e ->
                e.getCountry().equalsIgnoreCase(country)
        );
    }

    // Check if a date of birth exists among sanctioned entities
    public boolean isDobSanctioned(String dob) {
        return consolidatedList.stream().anyMatch(e ->
                e.getDob().equalsIgnoreCase(dob)
        );
    }

    //Checks if a sanctioning body is found among sanctioned entities.
    public boolean isSanctioningBodySanctioned(String sanctioningBody) {
        return consolidatedList.stream().anyMatch(e ->
                e.getSanctioningBody().equalsIgnoreCase(sanctioningBody)
        );
    }

    // Check if name contains partial word (optional)
    public boolean isNamePartiallySanctioned(String partial) {
        return consolidatedList.stream().anyMatch(e ->
                e.getName().toLowerCase().contains(partial.toLowerCase())
        );
    }

}
