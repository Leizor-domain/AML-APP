package com.leizo.service;

public interface SanctionsChecker {

    boolean isSanctionedEntity(String name, String country, String dob, String sanctioningBody);

    boolean checkCountry(String country);

    boolean checkName(String name);

    boolean checkPartialName(String partial);

    boolean checkSanctioningBody(String sanctioningBody);

}
