package com.leizo.admin.repository;

import com.leizo.pojo.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    // Find transactions by sender
    List<Transaction> findBySender(String sender);

    // Find transactions by receiver
    List<Transaction> findByReceiver(String receiver);

    // Find transactions by country
    List<Transaction> findByCountry(String country);

    // Find transactions by currency
    List<Transaction> findByCurrency(String currency);

    // Find transactions with amount greater than specified value
    List<Transaction> findByAmountGreaterThan(BigDecimal amount);

    // Find transactions by risk score
    List<Transaction> findByRiskScore(String riskScore);

    // Find transactions by sender and receiver
    List<Transaction> findBySenderAndReceiver(String sender, String receiver);

    // Find transactions by sender or receiver (either party)
    @Query("SELECT t FROM Transaction t WHERE t.sender = :party OR t.receiver = :party")
    List<Transaction> findByParty(@Param("party") String party);

    // Find high-value transactions (amount > 10000)
    @Query("SELECT t FROM Transaction t WHERE t.amount > 10000")
    List<Transaction> findHighValueTransactions();

    // Find transactions by date range - removed due to missing createdAt field
    // @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    // List<Transaction> findByDateRange(@Param("startDate") java.time.LocalDateTime startDate, 
    //                                  @Param("endDate") java.time.LocalDateTime endDate);

    // Count transactions by country
    @Query("SELECT t.country, COUNT(t) FROM Transaction t GROUP BY t.country")
    List<Object[]> countByCountry();

    // Sum amounts by currency
    @Query("SELECT t.currency, SUM(t.amount) FROM Transaction t GROUP BY t.currency")
    List<Object[]> sumAmountsByCurrency();
} 