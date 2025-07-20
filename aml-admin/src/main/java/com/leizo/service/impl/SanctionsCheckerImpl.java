package com.leizo.service.impl;

import com.leizo.loader.SanctionListLoader;
import com.leizo.service.SanctionsChecker;

public class SanctionsCheckerImpl implements SanctionsChecker {

    private final SanctionListLoader sanctionListLoader;

    public SanctionsCheckerImpl(SanctionListLoader sanctionListLoader) {
        this.sanctionListLoader = sanctionListLoader;
    }

    @Override
    public boolean isSanctionedEntity(String name, String country, String dob, String sanctioningBody) {
        // Body is optional — for multi-field exact match if needed later
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


}
