package com.leizo.model;

import com.leizo.enums.RiskScore;

import java.math.BigDecimal;
import java.util.Map;

public class Transaction {

    //declare variables
    private String sender;
    private String receiver;
    private BigDecimal amount;
    private String currency;
    private String country;
    private RiskScore riskScore;
    private Map<String, String> metadata;
    private String dob;


    //constructor
    public Transaction(String sender, String receiver,BigDecimal amount, String currency, String country, String dob) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.currency = currency;
        this.country = country;
        this.dob = dob;

    }

    public Transaction() {

    }

    //Setter
    public void setRiskScore(RiskScore riskScore) {
        this.riskScore = riskScore;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }


    //getters
    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public RiskScore getRiskScore() {
        return riskScore;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public String getDob() {
        return dob;
    }
    public void setDob(String dob) {
        this.dob = dob;
    }

}
