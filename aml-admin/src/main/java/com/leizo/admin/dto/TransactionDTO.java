package com.leizo.admin.dto;

import java.math.BigDecimal;

public class TransactionDTO {
    private String transactionId;
    private String timestamp; // ISO 8601
    private BigDecimal amount;
    private String currency;
    private String senderName;
    private String receiverName;
    private String senderAccount;
    private String receiverAccount;
    private String country;
    private Boolean manualFlag;
    private String description;

    // Getters and setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getSenderAccount() { return senderAccount; }
    public void setSenderAccount(String senderAccount) { this.senderAccount = senderAccount; }

    public String getReceiverAccount() { return receiverAccount; }
    public void setReceiverAccount(String receiverAccount) { this.receiverAccount = receiverAccount; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Boolean getManualFlag() { return manualFlag; }
    public void setManualFlag(Boolean manualFlag) { this.manualFlag = manualFlag; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
} 