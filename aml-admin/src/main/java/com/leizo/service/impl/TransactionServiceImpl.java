package com.leizo.service.impl;


import com.leizo.pojo.entity.Transaction;
import com.leizo.service.TransactionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
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
