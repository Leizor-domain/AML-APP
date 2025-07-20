package com.leizo.service.impl;


import com.leizo.model.Transaction;
import com.leizo.service.TransactionService;

import java.util.ArrayList;
import java.util.List;

public class TransactionServiceImpl implements TransactionService{

    private final List<Transaction> transactionLog = new ArrayList<>();

    @Override
    public void saveTransaction (Transaction txn){
        transactionLog.add(txn);
        System.out.println("[TransactionService] Transaction saved for: " + txn.getSender());
    }

    @Override
    public List<Transaction> getAllTransaction() {
        return transactionLog;
    }

}
