package com.leizo.admin.dto;

import com.leizo.pojo.entity.Transaction;
import java.util.HashMap;
import java.util.Map;

public class TransactionMapper {
    public static Transaction toEntity(TransactionDTO dto) {
        Transaction txn = new Transaction();
        // Do not set ID (auto-generated)
        txn.setSender(dto.getSenderName());
        txn.setReceiver(dto.getReceiverName());
        txn.setAmount(dto.getAmount());
        txn.setCurrency(dto.getCurrency());
        txn.setCountry(dto.getCountry());
        // Store additional fields in metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("transactionId", dto.getTransactionId());
        metadata.put("timestamp", dto.getTimestamp());
        metadata.put("senderAccount", dto.getSenderAccount());
        metadata.put("receiverAccount", dto.getReceiverAccount());
        metadata.put("manualFlag", String.valueOf(dto.getManualFlag()));
        if (dto.getDescription() != null) metadata.put("description", dto.getDescription());
        txn.setMetadata(metadata);
        return txn;
    }
} 