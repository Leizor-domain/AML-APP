package com.leizo.service;

import com.leizo.admin.entity.Transaction;

import java.math.BigDecimal;
import java.util.*;

public interface TransactionHistoryService {

    void saveTransaction(Transaction txn);

    List<Transaction> getTransactionHistory(String sender);

    BigDecimal getAverageAmount(String sender);

    int getFrequency(String sender); // can get a number of transaction in recent period

    List<Transaction> getAllTransactions();


}
