package com.leizo.admin.dto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TransactionCsvParser {
    private static final String[] EXPECTED_HEADERS = {
        "transactionId", "timestamp", "amount", "currency", "senderName", "receiverName", "senderAccount", "receiverAccount", "country", "manualFlag", "description"
    };
    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
    private static final DateTimeFormatter ISO_OFFSET_FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public static List<TransactionDTO> parse(InputStream inputStream, List<String> errors) throws IOException {
        List<TransactionDTO> dtos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String header = reader.readLine();
            if (header == null) {
                errors.add("CSV file is empty");
                return dtos;
            }
            String[] actualHeaders = header.split(",");
            if (actualHeaders.length < EXPECTED_HEADERS.length) {
                errors.add("CSV header is missing required columns. Expected: " + String.join(", ", EXPECTED_HEADERS));
                return dtos;
            }
            
            // Validate headers but don't fail completely - just warn
            for (int i = 0; i < EXPECTED_HEADERS.length; i++) {
                if (!actualHeaders[i].trim().equalsIgnoreCase(EXPECTED_HEADERS[i])) {
                    errors.add("CSV header column " + (i + 1) + " should be '" + EXPECTED_HEADERS[i] + "' but found '" + actualHeaders[i].trim() + "'");
                }
            }
            
            String line;
            int row = 1;
            while ((line = reader.readLine()) != null) {
                row++;
                String[] parts = line.split(",", -1);
                if (parts.length < EXPECTED_HEADERS.length) {
                    errors.add("Row " + row + ": Invalid CSV row - expected " + EXPECTED_HEADERS.length + " columns, found " + parts.length);
                    continue;
                }
                TransactionDTO dto = new TransactionDTO();
                boolean valid = true;
                
                // transactionId
                String transactionId = parts[0].trim();
                if (transactionId.isEmpty()) {
                    errors.add("Row " + row + ": transactionId cannot be empty");
                    valid = false;
                }
                dto.setTransactionId(transactionId);
                
                // timestamp
                String timestamp = parts[1].trim();
                if (timestamp.isEmpty()) {
                    errors.add("Row " + row + ": timestamp cannot be empty");
                    valid = false;
                } else {
                    try {
                        // Try both ISO formats
                        try {
                            ISO_FORMAT.parse(timestamp);
                        } catch (DateTimeParseException e1) {
                            try {
                                ISO_OFFSET_FORMAT.parse(timestamp);
                            } catch (DateTimeParseException e2) {
                                errors.add("Row " + row + ": timestamp must be in ISO 8601 format (e.g., 2025-07-25T14:22:30Z or 2025-07-25T14:22:30)");
                                valid = false;
                            }
                        }
                    } catch (Exception e) {
                        errors.add("Row " + row + ": timestamp must be in ISO 8601 format");
                        valid = false;
                    }
                }
                dto.setTimestamp(timestamp);
                
                // amount
                String amountStr = parts[2].trim();
                try {
                    dto.setAmount(new BigDecimal(amountStr));
                } catch (Exception e) {
                    errors.add("Row " + row + ": amount must be a valid decimal");
                    valid = false;
                }
                
                // currency
                String currency = parts[3].trim();
                if (currency.length() != 3) {
                    errors.add("Row " + row + ": currency must be 3-letter ISO 4217 code");
                    valid = false;
                }
                dto.setCurrency(currency.toUpperCase());
                
                // senderName
                String senderName = parts[4].trim();
                if (senderName.isEmpty()) {
                    errors.add("Row " + row + ": senderName cannot be empty");
                    valid = false;
                }
                dto.setSenderName(senderName);
                
                // receiverName
                String receiverName = parts[5].trim();
                if (receiverName.isEmpty()) {
                    errors.add("Row " + row + ": receiverName cannot be empty");
                    valid = false;
                }
                dto.setReceiverName(receiverName);
                
                // senderAccount
                String senderAccount = parts[6].trim();
                if (senderAccount.isEmpty()) {
                    errors.add("Row " + row + ": senderAccount cannot be empty");
                    valid = false;
                }
                dto.setSenderAccount(senderAccount);
                
                // receiverAccount
                String receiverAccount = parts[7].trim();
                if (receiverAccount.isEmpty()) {
                    errors.add("Row " + row + ": receiverAccount cannot be empty");
                    valid = false;
                }
                dto.setReceiverAccount(receiverAccount);
                
                // country
                String country = parts[8].trim();
                if (country.isEmpty()) {
                    errors.add("Row " + row + ": country cannot be empty");
                    valid = false;
                }
                dto.setCountry(country);
                
                // manualFlag
                String manualFlagStr = parts[9].trim();
                if (!manualFlagStr.equalsIgnoreCase("true") && !manualFlagStr.equalsIgnoreCase("false")) {
                    errors.add("Row " + row + ": manualFlag must be 'true' or 'false'");
                    valid = false;
                } else {
                    dto.setManualFlag(Boolean.parseBoolean(manualFlagStr));
                }
                
                // description (optional)
                dto.setDescription(parts[10].trim());
                
                if (valid) {
                    dtos.add(dto);
                }
            }
        }
        return dtos;
    }
} 