package com.leizo.service;

public interface OpenSanctionsService {
    /**
     * Checks if the given entity is sanctioned using the OpenSanctions API.
     * @param name Name of the entity (person or organization)
     * @param country Country of the entity
     * @param dob Date of birth (optional, can be null)
     * @return true if a match is found in OpenSanctions, false otherwise
     */
    boolean isEntitySanctioned(String name, String country, String dob);
} 