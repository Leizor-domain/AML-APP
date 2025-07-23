package com.leizo.service.impl;

import com.leizo.service.OpenSanctionsService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OpenSanctionsServiceImpl implements OpenSanctionsService {
    private static final String API_URL = "https://api.opensanctions.org/v1/entities/match";
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(OpenSanctionsServiceImpl.class);

    @Override
    public boolean isEntitySanctioned(String name, String country, String dob) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("name", name);
            payload.put("country", country);
            if (dob != null) payload.put("birthDate", dob);

            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, payload, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map body = response.getBody();
                if (body.containsKey("match") && Boolean.TRUE.equals(body.get("match"))) {
                    Map match = (Map) body.get("result");
                    logger.warn("OpenSanctions MATCH: Entity [{}] from [{}] (DOB: {}) is sanctioned! Details: {}", name, country, dob, match);
                    // Optionally, store match details for alert creation
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("Error calling OpenSanctions API for entity [{}]: {}", name, e.getMessage());
        }
        return false;
    }
} 