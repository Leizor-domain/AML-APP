package com.leizo.service.impl;

import com.leizo.loader.SanctionListLoader;
import com.leizo.service.SanctionsChecker;
import com.leizo.service.OfacXmlSanctionsApiClient;
import com.leizo.pojo.entity.Alert;
import com.leizo.admin.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SanctionsCheckerImpl implements SanctionsChecker {

    private static final Logger logger = LoggerFactory.getLogger(SanctionsCheckerImpl.class);

    private final SanctionListLoader sanctionListLoader;
    private final OfacXmlSanctionsApiClient ofacSanctionsClient;
    private final AlertRepository alertRepository;

    public SanctionsCheckerImpl(SanctionListLoader sanctionListLoader, OfacXmlSanctionsApiClient ofacSanctionsClient, AlertRepository alertRepository) {
        this.sanctionListLoader = sanctionListLoader;
        this.ofacSanctionsClient = ofacSanctionsClient;
        this.alertRepository = alertRepository;
    }

    @Override
    public boolean isSanctionedEntity(String name, String country, String dob, String sanctioningBody) {
        // First, check OFAC SDN list (primary source)
        if (ofacSanctionsClient.isEntitySanctioned(name, country)) {
            logger.warn("OFAC SANCTIONS MATCH: Entity [{}] from [{}] matched in OFAC SDN list", name, country);
            return true;
        }
        
        // Fallback to local list for additional sources
        if (sanctionListLoader.isEntitySanctioned(name, country, dob, "Any")) {
            logger.warn("LOCAL SANCTIONS MATCH: Entity [{}] from [{}] matched in local sanctions list", name, country);
            return true;
        }
        
        return false;
    }

    @Override
    public boolean checkCountry(String country) {
        return sanctionListLoader.isCountrySanctioned(country);
    }

    @Override
    public boolean checkName(String name) {
        return sanctionListLoader.isNameSanctioned(name);
    }

    @Override
    public boolean checkPartialName(String partial) {
        return sanctionListLoader.isNamePartiallySanctioned(partial);
    }

    @Override
    public boolean checkSanctioningBody(String sanctioningBody) {
        return sanctionListLoader.isSanctioningBodySanctioned(sanctioningBody);
    }

    public Alert checkAndAlertSanctionedEntity(String name, String country, String dob, String sanctioningBody, Integer transactionId) {
        // First, check OFAC SDN list (primary source)
        if (ofacSanctionsClient.isEntitySanctioned(name, country)) {
            Alert alert = new Alert();
            alert.setMatchedEntityName(name);
            alert.setMatchedList("OFAC_SDN");
            alert.setMatchReason("Matched by OFAC SDN real-time screening");
            alert.setTransactionId(transactionId);
            alert.setReason("MATCHED_SANCTIONED_ENTITY: OFAC SDN list match");
            alert.setTimestamp(LocalDateTime.now());
            alert.setAlertType("SANCTIONS");
            alert.setPriorityLevel("HIGH");
            alertRepository.save(alert);
            logger.warn("ALERT CREATED: Transaction [{}] flagged for OFAC sanctioned entity [{}]", transactionId, name);
            return alert;
        }
        
        // Fallback to local list
        if (sanctionListLoader.isEntitySanctioned(name, country, dob, "Any")) {
            Alert alert = new Alert();
            alert.setMatchedEntityName(name);
            alert.setMatchedList("LOCAL_SANCTIONS");
            alert.setMatchReason("Matched by local sanctions list");
            alert.setTransactionId(transactionId);
            alert.setReason("MATCHED_SANCTIONED_ENTITY: Local sanctions list match");
            alert.setTimestamp(LocalDateTime.now());
            alert.setAlertType("SANCTIONS");
            alert.setPriorityLevel("HIGH");
            alertRepository.save(alert);
            logger.warn("ALERT CREATED: Transaction [{}] flagged for locally sanctioned entity [{}]", transactionId, name);
            return alert;
        }
        
        return null;
    }

}
