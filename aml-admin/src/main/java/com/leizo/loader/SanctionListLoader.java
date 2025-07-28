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
            
            // Load comprehensive worldwide high-risk countries list
            loadComprehensiveHighRiskCountries();
            
            System.out.println("[SanctionListLoader] Loaded " + consolidatedList.size() + " sanctioned entities");
            System.out.println("[SanctionListLoader] Loaded " + highRiskCountries.size() + " high-risk countries");
            
        } catch (Exception e) {
            System.err.println("[SanctionListLoader] Failed to load lists: " + e.getMessage());
            // Load minimal test data if file loading fails
            loadMinimalTestData();
        }
    }

    /**
     * Loads a comprehensive list of high-risk countries worldwide based on:
     * - FATF (Financial Action Task Force) high-risk jurisdictions
     * - OFAC (Office of Foreign Assets Control) sanctioned countries
     * - EU high-risk third countries
     * - UN Security Council sanctions
     * - Global corruption indices
     * - Money laundering risk assessments
     */
    private void loadComprehensiveHighRiskCountries() {
        // FATF High-Risk Jurisdictions (2024)
        highRiskCountries.add("Myanmar");
        highRiskCountries.add("Democratic People's Republic of Korea");
        highRiskCountries.add("Iran");
        
        // FATF Jurisdictions Under Increased Monitoring
        highRiskCountries.add("Albania");
        highRiskCountries.add("Barbados");
        highRiskCountries.add("Burkina Faso");
        highRiskCountries.add("Cameroon");
        highRiskCountries.add("Cayman Islands");
        highRiskCountries.add("Croatia");
        highRiskCountries.add("Democratic Republic of the Congo");
        highRiskCountries.add("Gibraltar");
        highRiskCountries.add("Haiti");
        highRiskCountries.add("Jamaica");
        highRiskCountries.add("Jordan");
        highRiskCountries.add("Mali");
        highRiskCountries.add("Mozambique");
        highRiskCountries.add("Nigeria");
        highRiskCountries.add("Panama");
        highRiskCountries.add("Philippines");
        highRiskCountries.add("Senegal");
        highRiskCountries.add("South Africa");
        highRiskCountries.add("South Sudan");
        highRiskCountries.add("Syria");
        highRiskCountries.add("Tanzania");
        highRiskCountries.add("Turkey");
        highRiskCountries.add("Uganda");
        highRiskCountries.add("United Arab Emirates");
        highRiskCountries.add("Yemen");
        
        // OFAC Sanctioned Countries
        highRiskCountries.add("Cuba");
        highRiskCountries.add("Venezuela");
        highRiskCountries.add("Russia");
        highRiskCountries.add("Belarus");
        highRiskCountries.add("Zimbabwe");
        highRiskCountries.add("Sudan");
        highRiskCountries.add("Libya");
        highRiskCountries.add("Somalia");
        highRiskCountries.add("Central African Republic");
        highRiskCountries.add("Burundi");
        highRiskCountries.add("Eritrea");
        highRiskCountries.add("Guinea-Bissau");
        highRiskCountries.add("Iraq");
        highRiskCountries.add("Lebanon");
        highRiskCountries.add("Nicaragua");
        
        // EU High-Risk Third Countries
        highRiskCountries.add("Afghanistan");
        highRiskCountries.add("Botswana");
        highRiskCountries.add("Ghana");
        highRiskCountries.add("Pakistan");
        highRiskCountries.add("Trinidad and Tobago");
        highRiskCountries.add("Zimbabwe");
        
        // Additional High-Risk Countries (Corruption, Money Laundering, Terrorism)
        highRiskCountries.add("Angola");
        highRiskCountries.add("Bangladesh");
        highRiskCountries.add("Cambodia");
        highRiskCountries.add("Chad");
        highRiskCountries.add("Comoros");
        highRiskCountries.add("Congo");
        highRiskCountries.add("Djibouti");
        highRiskCountries.add("Equatorial Guinea");
        highRiskCountries.add("Eswatini");
        highRiskCountries.add("Ethiopia");
        highRiskCountries.add("Gabon");
        highRiskCountries.add("Gambia");
        highRiskCountries.add("Guinea");
        highRiskCountries.add("Kazakhstan");
        highRiskCountries.add("Kenya");
        highRiskCountries.add("Kyrgyzstan");
        highRiskCountries.add("Laos");
        highRiskCountries.add("Liberia");
        highRiskCountries.add("Madagascar");
        highRiskCountries.add("Malawi");
        highRiskCountries.add("Mauritania");
        highRiskCountries.add("Mauritius");
        highRiskCountries.add("Mongolia");
        highRiskCountries.add("Myanmar");
        highRiskCountries.add("Nepal");
        highRiskCountries.add("Niger");
        highRiskCountries.add("Papua New Guinea");
        highRiskCountries.add("Paraguay");
        highRiskCountries.add("Rwanda");
        highRiskCountries.add("Sierra Leone");
        highRiskCountries.add("Sri Lanka");
        highRiskCountries.add("Tajikistan");
        highRiskCountries.add("Togo");
        highRiskCountries.add("Turkmenistan");
        highRiskCountries.add("Uzbekistan");
        highRiskCountries.add("Vietnam");
        highRiskCountries.add("Zambia");
        
        // Tax Havens and Offshore Financial Centers
        highRiskCountries.add("Andorra");
        highRiskCountries.add("Antigua and Barbuda");
        highRiskCountries.add("Aruba");
        highRiskCountries.add("Bahamas");
        highRiskCountries.add("Bahrain");
        highRiskCountries.add("Belize");
        highRiskCountries.add("Bermuda");
        highRiskCountries.add("British Virgin Islands");
        highRiskCountries.add("Cook Islands");
        highRiskCountries.add("Costa Rica");
        highRiskCountries.add("Cyprus");
        highRiskCountries.add("Dominica");
        highRiskCountries.add("Dominican Republic");
        highRiskCountries.add("Grenada");
        highRiskCountries.add("Guernsey");
        highRiskCountries.add("Isle of Man");
        highRiskCountries.add("Jersey");
        highRiskCountries.add("Liechtenstein");
        highRiskCountries.add("Luxembourg");
        highRiskCountries.add("Marshall Islands");
        highRiskCountries.add("Monaco");
        highRiskCountries.add("Montserrat");
        highRiskCountries.add("Nauru");
        highRiskCountries.add("Netherlands Antilles");
        highRiskCountries.add("Niue");
        highRiskCountries.add("Palau");
        highRiskCountries.add("Saint Kitts and Nevis");
        highRiskCountries.add("Saint Lucia");
        highRiskCountries.add("Saint Vincent and the Grenadines");
        highRiskCountries.add("Samoa");
        highRiskCountries.add("San Marino");
        highRiskCountries.add("Seychelles");
        highRiskCountries.add("Switzerland");
        highRiskCountries.add("Tonga");
        highRiskCountries.add("Vanuatu");
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
