package com.leizo.model;

public class SanctionedEntity {
    private String name;
    private String country;
    private String dob;
    private String sanctioningBody;

    public SanctionedEntity(String name, String country, String dob, String sanctioningBody) {
        this.name = name;
        this.country = country;
        this.dob  = dob;
        this.sanctioningBody = sanctioningBody;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getDob() {
        return dob;
    }

    public String getSanctioningBody() {
        return sanctioningBody;
    }

    @Override
    public String toString() {
        return "SanctionedEntity{name='" + name + "', country='" + country + "', dob='" + dob + "', sanctioning Body='" + sanctioningBody + "'}";
    }
}
