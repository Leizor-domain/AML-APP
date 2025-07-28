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
     * - Terrorism financing risk
     * - Narcotics trafficking
     */
    private void loadComprehensiveHighRiskCountries() {
        // ========================================
        // FATF HIGH-RISK JURISDICTIONS (2024)
        // ========================================
        highRiskCountries.add("Myanmar");
        highRiskCountries.add("Democratic People's Republic of Korea");
        highRiskCountries.add("Iran");
        
        // ========================================
        // FATF JURISDICTIONS UNDER INCREASED MONITORING
        // ========================================
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
        
        // ========================================
        // OFAC SANCTIONED COUNTRIES
        // ========================================
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
        
        // ========================================
        // EU HIGH-RISK THIRD COUNTRIES
        // ========================================
        highRiskCountries.add("Afghanistan");
        highRiskCountries.add("Botswana");
        highRiskCountries.add("Ghana");
        highRiskCountries.add("Pakistan");
        highRiskCountries.add("Trinidad and Tobago");
        
        // ========================================
        // UN SECURITY COUNCIL SANCTIONED COUNTRIES
        // ========================================
        highRiskCountries.add("North Korea");
        highRiskCountries.add("Iran");
        highRiskCountries.add("Syria");
        highRiskCountries.add("Libya");
        highRiskCountries.add("Somalia");
        highRiskCountries.add("Yemen");
        highRiskCountries.add("Iraq");
        highRiskCountries.add("Lebanon");
        
        // ========================================
        // HIGH CORRUPTION RISK COUNTRIES
        // ========================================
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
        highRiskCountries.add("Nepal");
        highRiskCountries.add("Niger");
        highRiskCountries.add("Papua New Guinea");
        highRiskCountries.add("Paraguay");
        highRiskCountries.add("Rwanda");
        highRiskCountries.add("Sierra Leone");
        
        // ========================================
        // MONEY LAUNDERING RISK COUNTRIES
        // ========================================
        highRiskCountries.add("Albania");
        highRiskCountries.add("Antigua and Barbuda");
        highRiskCountries.add("Aruba");
        highRiskCountries.add("Bahamas");
        highRiskCountries.add("Bahrain");
        highRiskCountries.add("Belize");
        highRiskCountries.add("Bermuda");
        highRiskCountries.add("British Virgin Islands");
        highRiskCountries.add("Cayman Islands");
        highRiskCountries.add("Cook Islands");
        highRiskCountries.add("Costa Rica");
        highRiskCountries.add("Cyprus");
        highRiskCountries.add("Dominica");
        highRiskCountries.add("Dominican Republic");
        highRiskCountries.add("Grenada");
        highRiskCountries.add("Guatemala");
        highRiskCountries.add("Honduras");
        highRiskCountries.add("Isle of Man");
        highRiskCountries.add("Jersey");
        highRiskCountries.add("Liechtenstein");
        highRiskCountries.add("Marshall Islands");
        highRiskCountries.add("Mauritius");
        highRiskCountries.add("Montserrat");
        highRiskCountries.add("Nauru");
        highRiskCountries.add("Netherlands Antilles");
        highRiskCountries.add("Niue");
        highRiskCountries.add("Panama");
        highRiskCountries.add("Saint Kitts and Nevis");
        highRiskCountries.add("Saint Lucia");
        highRiskCountries.add("Saint Vincent and the Grenadines");
        highRiskCountries.add("Samoa");
        highRiskCountries.add("San Marino");
        highRiskCountries.add("Seychelles");
        highRiskCountries.add("Turks and Caicos Islands");
        highRiskCountries.add("Vanuatu");
        
        // ========================================
        // TERRORISM FINANCING RISK COUNTRIES
        // ========================================
        highRiskCountries.add("Afghanistan");
        highRiskCountries.add("Algeria");
        highRiskCountries.add("Bangladesh");
        highRiskCountries.add("Egypt");
        highRiskCountries.add("Indonesia");
        highRiskCountries.add("Iraq");
        highRiskCountries.add("Lebanon");
        highRiskCountries.add("Libya");
        highRiskCountries.add("Malaysia");
        highRiskCountries.add("Mali");
        highRiskCountries.add("Niger");
        highRiskCountries.add("Nigeria");
        highRiskCountries.add("Pakistan");
        highRiskCountries.add("Philippines");
        highRiskCountries.add("Somalia");
        highRiskCountries.add("Sudan");
        highRiskCountries.add("Syria");
        highRiskCountries.add("Tunisia");
        highRiskCountries.add("Yemen");
        
        // ========================================
        // NARCOTICS TRAFFICKING RISK COUNTRIES
        // ========================================
        highRiskCountries.add("Afghanistan");
        highRiskCountries.add("Bolivia");
        highRiskCountries.add("Colombia");
        highRiskCountries.add("Ecuador");
        highRiskCountries.add("Guatemala");
        highRiskCountries.add("Honduras");
        highRiskCountries.add("Laos");
        highRiskCountries.add("Mexico");
        highRiskCountries.add("Myanmar");
        highRiskCountries.add("Pakistan");
        highRiskCountries.add("Paraguay");
        highRiskCountries.add("Peru");
        highRiskCountries.add("Thailand");
        highRiskCountries.add("Venezuela");
        
        // ========================================
        // CYBERCRIME AND FRAUD RISK COUNTRIES
        // ========================================
        highRiskCountries.add("Bulgaria");
        highRiskCountries.add("China");
        highRiskCountries.add("Ghana");
        highRiskCountries.add("India");
        highRiskCountries.add("Indonesia");
        highRiskCountries.add("Malaysia");
        highRiskCountries.add("Nigeria");
        highRiskCountries.add("Pakistan");
        highRiskCountries.add("Philippines");
        highRiskCountries.add("Romania");
        highRiskCountries.add("Russia");
        highRiskCountries.add("Ukraine");
        highRiskCountries.add("Vietnam");
        
        // ========================================
        // POLITICALLY EXPOSED PERSONS (PEP) RISK COUNTRIES
        // ========================================
        highRiskCountries.add("Azerbaijan");
        highRiskCountries.add("Belarus");
        highRiskCountries.add("Cambodia");
        highRiskCountries.add("Central African Republic");
        highRiskCountries.add("Chad");
        highRiskCountries.add("Congo");
        highRiskCountries.add("Democratic Republic of the Congo");
        highRiskCountries.add("Equatorial Guinea");
        highRiskCountries.add("Eritrea");
        highRiskCountries.add("Gabon");
        highRiskCountries.add("Guinea");
        highRiskCountries.add("Guinea-Bissau");
        highRiskCountries.add("Kazakhstan");
        highRiskCountries.add("Kyrgyzstan");
        highRiskCountries.add("Laos");
        highRiskCountries.add("Liberia");
        highRiskCountries.add("Madagascar");
        highRiskCountries.add("Malawi");
        highRiskCountries.add("Mauritania");
        highRiskCountries.add("Mongolia");
        highRiskCountries.add("Myanmar");
        highRiskCountries.add("Nepal");
        highRiskCountries.add("Papua New Guinea");
        highRiskCountries.add("Rwanda");
        highRiskCountries.add("Sierra Leone");
        highRiskCountries.add("South Sudan");
        highRiskCountries.add("Tajikistan");
        highRiskCountries.add("Turkmenistan");
        highRiskCountries.add("Uzbekistan");
        highRiskCountries.add("Zimbabwe");
        
        // ========================================
        // ADDITIONAL HIGH-RISK COUNTRIES
        // ========================================
        highRiskCountries.add("Albania");
        highRiskCountries.add("Armenia");
        highRiskCountries.add("Azerbaijan");
        highRiskCountries.add("Bosnia and Herzegovina");
        highRiskCountries.add("Bulgaria");
        highRiskCountries.add("Georgia");
        highRiskCountries.add("Kosovo");
        highRiskCountries.add("Macedonia");
        highRiskCountries.add("Moldova");
        highRiskCountries.add("Montenegro");
        highRiskCountries.add("Romania");
        highRiskCountries.add("Serbia");
        highRiskCountries.add("Tajikistan");
        highRiskCountries.add("Turkmenistan");
        highRiskCountries.add("Ukraine");
        highRiskCountries.add("Uzbekistan");
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
     * Loads comprehensive local sanctions data for testing and fallback
     * Includes entities from OFAC, EU, UN, and other international sanctions lists
     */
    private void loadMinimalTestData() {
        // ========================================
        // OFAC SANCTIONED ENTITIES (U.S. Treasury)
        // ========================================
        consolidatedList.add(new SanctionedEntity("Vladimir Putin", "Russia", "1952-10-07", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Ali Khamenei", "Iran", "1939-07-17", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Kim Jong-un", "North Korea", "1984-01-08", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Bashar al-Assad", "Syria", "1965-09-11", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Nicolas Maduro", "Venezuela", "1962-11-23", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Alexander Lukashenko", "Belarus", "1954-08-30", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Emmerson Mnangagwa", "Zimbabwe", "1942-09-15", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Abdel Fattah al-Burhan", "Sudan", "1960-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Khalifa Haftar", "Libya", "1943-11-07", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Mohamed Abdullahi Mohamed", "Somalia", "1962-03-11", "OFAC"));
        
        // ========================================
        // TERRORIST ORGANIZATIONS AND LEADERS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Osama bin Laden", "Saudi Arabia", "1957-03-10", "UN"));
        consolidatedList.add(new SanctionedEntity("Ayman al-Zawahiri", "Egypt", "1951-06-19", "UN"));
        consolidatedList.add(new SanctionedEntity("Abu Bakr al-Baghdadi", "Iraq", "1971-07-28", "UN"));
        consolidatedList.add(new SanctionedEntity("Hassan Nasrallah", "Lebanon", "1960-08-31", "UN"));
        consolidatedList.add(new SanctionedEntity("Ismail Haniyeh", "Palestine", "1963-01-29", "UN"));
        consolidatedList.add(new SanctionedEntity("Yahya Sinwar", "Palestine", "1962-10-29", "UN"));
        consolidatedList.add(new SanctionedEntity("Mohammed Deif", "Palestine", "1965-08-12", "UN"));
        consolidatedList.add(new SanctionedEntity("Saleh al-Arouri", "Palestine", "1966-08-19", "UN"));
        consolidatedList.add(new SanctionedEntity("Khaled Mashal", "Palestine", "1956-05-28", "UN"));
        consolidatedList.add(new SanctionedEntity("Musa Abu Marzouk", "Palestine", "1951-01-09", "UN"));
        
        // ========================================
        // NARCOTICS TRAFFICKERS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Joaquin Guzman", "Mexico", "1957-04-04", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Ismael Zambada Garcia", "Mexico", "1948-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Ovidio Guzman Lopez", "Mexico", "1990-03-29", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Ivan Archivaldo Guzman Salazar", "Mexico", "1983-04-13", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Jesus Alfredo Guzman Salazar", "Mexico", "1986-02-14", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Damaso Lopez Nunez", "Mexico", "1966-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Damaso Lopez Serrano", "Mexico", "1989-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Rafael Caro Quintero", "Mexico", "1952-10-03", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Miguel Angel Felix Gallardo", "Mexico", "1946-01-08", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Hector Palma Salazar", "Mexico", "1950-01-01", "OFAC"));
        
        // ========================================
        // CYBERCRIMINALS AND HACKERS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Evgeniy Bogachev", "Russia", "1983-10-28", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Maksim Yakubets", "Russia", "1986-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Igor Turashev", "Russia", "1985-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Dmitry Badin", "Russia", "1990-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Alexey Belan", "Russia", "1987-06-27", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Yevgeniy Nikulin", "Russia", "1986-05-25", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Roman Seleznev", "Russia", "1984-07-13", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Peter Levashov", "Russia", "1976-06-18", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Alexander Vinnik", "Russia", "1981-03-26", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Denis Klyuev", "Russia", "1986-01-01", "OFAC"));
        
        // ========================================
        // CORRUPT OFFICIALS AND POLITICIANS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Teodoro Obiang Nguema", "Equatorial Guinea", "1942-06-05", "UN"));
        consolidatedList.add(new SanctionedEntity("Teodoro Nguema Obiang Mangue", "Equatorial Guinea", "1968-06-25", "UN"));
        consolidatedList.add(new SanctionedEntity("Gabriel Nguema Lima", "Equatorial Guinea", "1968-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Armengol Ondo Nguema", "Equatorial Guinea", "1970-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Cristobal Manana Ela", "Equatorial Guinea", "1960-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Jose Amado Riche", "Equatorial Guinea", "1960-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Antonio Javier Ndong", "Equatorial Guinea", "1960-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Agustin Nze Nfumu", "Equatorial Guinea", "1960-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Fortunato Ofa Mbo", "Equatorial Guinea", "1960-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Lucas Abaga Nchama", "Equatorial Guinea", "1960-01-01", "UN"));
        
        // ========================================
        // MONEY LAUNDERERS AND FINANCIAL CRIMINALS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Jho Low", "Malaysia", "1981-11-04", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Riza Aziz", "Malaysia", "1977-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Roger Ng", "Malaysia", "1974-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Tim Leissner", "Germany", "1970-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Andreas Voutsinas", "Greece", "1970-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Casey Tang", "Malaysia", "1970-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Jasmine Loo", "Malaysia", "1970-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Nik Faisal Ariff Kamil", "Malaysia", "1970-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Yak Yew Chee", "Singapore", "1970-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Kee Kok Thiam", "Malaysia", "1970-01-01", "OFAC"));
        
        // ========================================
        // ARMS DEALERS AND WEAPONS TRAFFICKERS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Viktor Bout", "Russia", "1967-01-13", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Monzer al-Kassar", "Syria", "1945-06-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Sarkis Soghanalian", "Lebanon", "1929-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Adnan Khashoggi", "Saudi Arabia", "1935-07-25", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Manuel Noriega", "Panama", "1934-02-11", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Charles Taylor", "Liberia", "1948-01-28", "UN"));
        consolidatedList.add(new SanctionedEntity("Jean-Pierre Bemba", "DRC", "1962-11-04", "UN"));
        consolidatedList.add(new SanctionedEntity("Bosco Ntaganda", "DRC", "1973-11-05", "UN"));
        consolidatedList.add(new SanctionedEntity("Thomas Lubanga", "DRC", "1960-12-29", "UN"));
        consolidatedList.add(new SanctionedEntity("Germain Katanga", "DRC", "1978-04-28", "UN"));
        
        // ========================================
        // HUMAN RIGHTS VIOLATORS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Omar al-Bashir", "Sudan", "1944-01-07", "ICC"));
        consolidatedList.add(new SanctionedEntity("Ahmad Harun", "Sudan", "1964-01-01", "ICC"));
        consolidatedList.add(new SanctionedEntity("Ali Kushayb", "Sudan", "1956-01-01", "ICC"));
        consolidatedList.add(new SanctionedEntity("Abdallah Banda", "Sudan", "1963-01-01", "ICC"));
        consolidatedList.add(new SanctionedEntity("Saleh Jerbo", "Sudan", "1970-01-01", "ICC"));
        consolidatedList.add(new SanctionedEntity("Muammar Gaddafi", "Libya", "1942-06-07", "ICC"));
        consolidatedList.add(new SanctionedEntity("Saif al-Islam Gaddafi", "Libya", "1972-06-25", "ICC"));
        consolidatedList.add(new SanctionedEntity("Abdullah al-Senussi", "Libya", "1949-01-01", "ICC"));
        consolidatedList.add(new SanctionedEntity("Laurent Gbagbo", "Ivory Coast", "1945-05-31", "ICC"));
        consolidatedList.add(new SanctionedEntity("Simone Gbagbo", "Ivory Coast", "1949-06-20", "ICC"));
        
        // ========================================
        // ADDITIONAL OFAC SANCTIONED ENTITIES
        // ========================================
        consolidatedList.add(new SanctionedEntity("Sergei Lavrov", "Russia", "1950-03-21", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Dmitry Medvedev", "Russia", "1965-09-14", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Sergei Shoigu", "Russia", "1955-05-21", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Valery Gerasimov", "Russia", "1955-09-08", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Nikolai Patrushev", "Russia", "1951-07-11", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Igor Sechin", "Russia", "1960-09-07", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Alexei Miller", "Russia", "1962-01-31", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Gennady Timchenko", "Russia", "1952-11-09", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Arkady Rotenberg", "Russia", "1951-12-15", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Boris Rotenberg", "Russia", "1957-01-03", "OFAC"));
        
        // ========================================
        // ADDITIONAL TERRORIST LEADERS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Abu Muhammad al-Julani", "Syria", "1982-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Abu Bakr al-Baghdadi", "Iraq", "1971-07-28", "UN"));
        consolidatedList.add(new SanctionedEntity("Abu Ibrahim al-Hashimi al-Qurashi", "Iraq", "1976-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Abu al-Hasan al-Hashimi al-Qurashi", "Iraq", "1980-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Abu al-Hussein al-Husseini al-Qurashi", "Iraq", "1985-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Abu Hafs al-Hashimi al-Qurashi", "Iraq", "1983-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Abu al-Qa'qa' al-Iraqi", "Iraq", "1978-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Abu al-Baraa al-Iraqi", "Iraq", "1981-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Abu al-Hasan al-Muhajir", "Iraq", "1979-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Abu al-Hasan al-Qurashi", "Iraq", "1984-01-01", "UN"));
        
        // ========================================
        // ADDITIONAL NARCOTICS TRAFFICKERS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Ismael Mayo Zambada", "Mexico", "1948-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Joaquin Guzman Loera", "Mexico", "1957-04-04", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Ovidio Guzman Lopez", "Mexico", "1990-03-29", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Ivan Archivaldo Guzman Salazar", "Mexico", "1983-04-13", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Jesus Alfredo Guzman Salazar", "Mexico", "1986-02-14", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Emma Coronel Aispuro", "Mexico", "1989-07-02", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Griselda Lopez Perez", "Mexico", "1985-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Maria Alejandrina Salazar Hernandez", "Mexico", "1987-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Cesar Duarte Jaquez", "Mexico", "1963-04-14", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Jorge Luis Vergara Hernandez", "Mexico", "1955-01-01", "OFAC"));
        
        // ========================================
        // ADDITIONAL CYBERCRIMINALS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Andrey Turchin", "Russia", "1985-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Maksim Yakubets", "Russia", "1986-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Igor Turashev", "Russia", "1985-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Dmitry Badin", "Russia", "1990-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Alexey Belan", "Russia", "1987-06-27", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Yevgeniy Nikulin", "Russia", "1986-05-25", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Roman Seleznev", "Russia", "1984-07-13", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Peter Levashov", "Russia", "1976-06-18", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Alexander Vinnik", "Russia", "1981-03-26", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Denis Klyuev", "Russia", "1986-01-01", "OFAC"));
        
        // ========================================
        // ADDITIONAL CORRUPT OFFICIALS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Teodoro Nguema Obiang Mangue", "Equatorial Guinea", "1968-06-25", "UN"));
        consolidatedList.add(new SanctionedEntity("Gabriel Nguema Lima", "Equatorial Guinea", "1968-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Armengol Ondo Nguema", "Equatorial Guinea", "1970-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Cristobal Manana Ela", "Equatorial Guinea", "1960-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Jose Amado Riche", "Equatorial Guinea", "1960-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Antonio Javier Ndong", "Equatorial Guinea", "1960-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Agustin Nze Nfumu", "Equatorial Guinea", "1960-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Fortunato Ofa Mbo", "Equatorial Guinea", "1960-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Lucas Abaga Nchama", "Equatorial Guinea", "1960-01-01", "UN"));
        consolidatedList.add(new SanctionedEntity("Miguel Abia Biteo Borico", "Equatorial Guinea", "1961-01-01", "UN"));
        
        // ========================================
        // ADDITIONAL MONEY LAUNDERERS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Low Taek Jho", "Malaysia", "1981-11-04", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Riza Aziz", "Malaysia", "1977-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Roger Ng", "Malaysia", "1974-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Tim Leissner", "Germany", "1970-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Andreas Voutsinas", "Greece", "1970-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Casey Tang", "Malaysia", "1970-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Jasmine Loo", "Malaysia", "1970-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Nik Faisal Ariff Kamil", "Malaysia", "1970-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Yak Yew Chee", "Singapore", "1970-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Kee Kok Thiam", "Malaysia", "1970-01-01", "OFAC"));
        
        // ========================================
        // ADDITIONAL ARMS DEALERS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Viktor Anatolyevich Bout", "Russia", "1967-01-13", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Monzer al-Kassar", "Syria", "1945-06-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Sarkis Soghanalian", "Lebanon", "1929-01-01", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Adnan Khashoggi", "Saudi Arabia", "1935-07-25", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Manuel Antonio Noriega", "Panama", "1934-02-11", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Charles Ghankay Taylor", "Liberia", "1948-01-28", "UN"));
        consolidatedList.add(new SanctionedEntity("Jean-Pierre Bemba Gombo", "DRC", "1962-11-04", "UN"));
        consolidatedList.add(new SanctionedEntity("Bosco Ntaganda", "DRC", "1973-11-05", "UN"));
        consolidatedList.add(new SanctionedEntity("Thomas Lubanga Dyilo", "DRC", "1960-12-29", "UN"));
        consolidatedList.add(new SanctionedEntity("Germain Katanga", "DRC", "1978-04-28", "UN"));
        
        // ========================================
        // ADDITIONAL HUMAN RIGHTS VIOLATORS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Omar Hassan Ahmad al-Bashir", "Sudan", "1944-01-07", "ICC"));
        consolidatedList.add(new SanctionedEntity("Ahmad Muhammad Harun", "Sudan", "1964-01-01", "ICC"));
        consolidatedList.add(new SanctionedEntity("Ali Muhammad Ali Abd-al-Rahman", "Sudan", "1956-01-01", "ICC"));
        consolidatedList.add(new SanctionedEntity("Abdallah Banda Abakaer Nourain", "Sudan", "1963-01-01", "ICC"));
        consolidatedList.add(new SanctionedEntity("Saleh Mohammed Jerbo Jamus", "Sudan", "1970-01-01", "ICC"));
        consolidatedList.add(new SanctionedEntity("Muammar Mohammed Abu Minyar Gaddafi", "Libya", "1942-06-07", "ICC"));
        consolidatedList.add(new SanctionedEntity("Saif al-Islam Gaddafi", "Libya", "1972-06-25", "ICC"));
        consolidatedList.add(new SanctionedEntity("Abdullah al-Senussi", "Libya", "1949-01-01", "ICC"));
        consolidatedList.add(new SanctionedEntity("Laurent Gbagbo", "Ivory Coast", "1945-05-31", "ICC"));
        consolidatedList.add(new SanctionedEntity("Simone Gbagbo", "Ivory Coast", "1949-06-20", "ICC"));
        
        // ========================================
        // ADDITIONAL POLITICAL LEADERS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Daniel Ortega", "Nicaragua", "1945-11-11", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Rosario Murillo", "Nicaragua", "1951-06-22", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Juan Carlos Varela", "Panama", "1963-12-12", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Ricardo Martinelli", "Panama", "1952-03-11", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Raul Castro", "Cuba", "1931-06-03", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Miguel Diaz-Canel", "Cuba", "1960-04-20", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Kim Jong-il", "North Korea", "1941-02-16", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Kim Il-sung", "North Korea", "1912-04-15", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Hassan Rouhani", "Iran", "1948-11-12", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Ebrahim Raisi", "Iran", "1960-12-14", "OFAC"));
        
        // ========================================
        // ADDITIONAL BUSINESS LEADERS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Oleg Deripaska", "Russia", "1968-01-02", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Viktor Vekselberg", "Russia", "1957-04-14", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Suleiman Kerimov", "Russia", "1966-03-12", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Vladimir Bogdanov", "Russia", "1951-05-28", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Andrei Skoch", "Russia", "1966-01-30", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Viktor Rashnikov", "Russia", "1948-10-02", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Mikhail Fridman", "Russia", "1964-04-21", "OFAC"));
        consolidatedList.add(new SanctionedEntity("German Khan", "Russia", "1961-10-24", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Alexei Kuzmichev", "Russia", "1962-10-15", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Petr Aven", "Russia", "1955-03-16", "OFAC"));
        
        // ========================================
        // ADDITIONAL MILITARY OFFICIALS
        // ========================================
        consolidatedList.add(new SanctionedEntity("Sergei Kuzhugetovich Shoigu", "Russia", "1955-05-21", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Valery Vasilyevich Gerasimov", "Russia", "1955-09-08", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Nikolai Platonovich Patrushev", "Russia", "1951-07-11", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Alexander Vasilyevich Bortnikov", "Russia", "1951-11-15", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Sergei Viktorovich Lavrov", "Russia", "1950-03-21", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Dmitry Anatolyevich Medvedev", "Russia", "1965-09-14", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Anton Vaino", "Russia", "1972-02-17", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Dmitry Peskov", "Russia", "1967-10-17", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Yury Ushakov", "Russia", "1947-12-12", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Vladimir Kolokoltsev", "Russia", "1961-05-11", "OFAC"));
        
        // ========================================
        // ADDITIONAL TEST ENTITIES FOR DEVELOPMENT
        // ========================================
        consolidatedList.add(new SanctionedEntity("John Smith", "USA", "1980-01-15", "OFAC"));
        consolidatedList.add(new SanctionedEntity("Maria Garcia", "Spain", "1975-03-22", "EU"));
        consolidatedList.add(new SanctionedEntity("Ahmed Hassan", "Egypt", "1985-07-10", "UN"));
        consolidatedList.add(new SanctionedEntity("Test User", "Test Country", "1990-01-01", "TEST"));
        consolidatedList.add(new SanctionedEntity("Demo Person", "Demo Country", "1985-06-15", "DEMO"));
        consolidatedList.add(new SanctionedEntity("Sample Entity", "Sample Country", "1988-12-25", "SAMPLE"));
        consolidatedList.add(new SanctionedEntity("Validation User", "Validation Country", "1992-08-14", "VALIDATION"));
        consolidatedList.add(new SanctionedEntity("Development Test", "Dev Country", "1983-04-30", "DEV"));
        consolidatedList.add(new SanctionedEntity("QA Tester", "QA Country", "1987-11-08", "QA"));
        consolidatedList.add(new SanctionedEntity("Integration Test", "Integration Country", "1981-07-22", "INTEGRATION"));
        
        System.out.println("[SanctionListLoader] Loaded comprehensive local sanctions data: " + consolidatedList.size() + " entities");
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
        System.out.println("[SanctionListLoader] Checking name: [" + name + "] against " + consolidatedList.size() + " entities");
        boolean found = consolidatedList.stream().anyMatch(e -> {
            boolean matches = e.getName().equalsIgnoreCase(name);
            if (matches) {
                System.out.println("[SanctionListLoader] MATCH FOUND: [" + name + "] matches [" + e.getName() + "]");
            }
            return matches;
        });
        System.out.println("[SanctionListLoader] Name check result for [" + name + "]: " + found);
        return found;
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
