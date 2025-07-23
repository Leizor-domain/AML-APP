package com.leizo.service;

import com.leizo.admin.entity.Transaction;
import java.util.List;

public interface TransactionService {

    void saveTransaction(Transaction txn);
    List<Transaction> getAllTransaction();
}
