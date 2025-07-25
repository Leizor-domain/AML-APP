package com.leizo.service.impl;

import com.leizo.loader.SanctionListLoader;
import com.leizo.service.SanctionsChecker;
import com.leizo.service.OpenSanctionsService;
import com.leizo.admin.entity.Alert;
import com.leizo.admin.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SanctionsCheckerImpl implements SanctionsChecker {

    private static final Logger logger = LoggerFactory.getLogger(SanctionsCheckerImpl.class);

    private final SanctionListLoader sanctionListLoader;
    private final OpenSanctionsService openSanctionsService;
    private final AlertRepository alertRepository;

    public SanctionsCheckerImpl(SanctionListLoader sanctionListLoader, OpenSanctionsService openSanctionsService, AlertRepository alertRepository) {
        this.sanctionListLoader = sanctionListLoader;
        this.openSanctionsService = openSanctionsService;
        this.alertRepository = alertRepository;
    }

    @Override
    public boolean isSanctionedEntity(String name, String country, String dob, String sanctioningBody) {
        // First, check OpenSanctions API
        if (openSanctionsService.isEntitySanctioned(name, country, dob)) {
            return true;
        }
        // Fallback to local list
        return sanctionListLoader.isEntitySanctioned(name, country, dob, "Any");
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
        // First, check OpenSanctions API
        if (openSanctionsService.isEntitySanctioned(name, country, dob)) {
            Alert alert = new Alert();
            alert.setMatchedEntityName(name);
            alert.setMatchedList("OpenSanctions");
            alert.setMatchReason("Matched by OpenSanctions real-time screening");
            alert.setTransactionId(transactionId);
            alert.setReason("Matched by OpenSanctions real-time screening");
            alert.setTimestamp(LocalDateTime.now());
            alert.setAlertType("SANCTIONS");
            alert.setPriorityLevel("HIGH");
            alertRepository.save(alert);
            logger.warn("ALERT CREATED: Transaction [{}] flagged for sanctioned entity [{}]", transactionId, name);
            return alert;
        }
        // Fallback to local list
        return null;
    }

}
