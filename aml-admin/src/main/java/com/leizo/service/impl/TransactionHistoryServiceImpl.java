package com.leizo.service.impl;

import com.leizo.admin.entity.Transaction;
import com.leizo.enums.RiskScore;
import com.leizo.service.TransactionHistoryService;

import org.springframework.beans.factory.annotation.Value;
import java.sql.*;
import java.util.*;
import java.math.*;

public class TransactionHistoryServiceImpl implements TransactionHistoryService {

    @Value("${spring.datasource.url}")
    private String URL;
    @Value("${spring.datasource.username}")
    private String USER;
    @Value("${spring.datasource.password}")
    private String PASSWORD;


    @Override
    public void saveTransaction(Transaction txn) {
        String sql = "INSERT INTO transactions (sender, receiver, amount, currency, country, risk_score) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txn.getSender());
            ps.setString(2, txn.getReceiver());
            ps.setBigDecimal(3, txn.getAmount());
            ps.setString(4, txn.getCurrency());
            ps.setString(5, txn.getCountry());
            ps.setString(6, txn.getRiskScore() != null ? txn.getRiskScore().name() : null);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Transaction> getTransactionHistory(String sender) {
        List<Transaction> result = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE sender = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sender);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaction txn = new Transaction(
                        rs.getString("sender"),
                        rs.getString("receiver"),
                        rs.getBigDecimal("amount"),
                        rs.getString("currency"),
                        rs.getString("country"),
                        null // or rs.getString("dob") if your schema has it
                );
                // set risk score
                if (rs.getString("risk_score") != null) {
                    txn.setRiskScore(RiskScore.valueOf(rs.getString("risk_score")));
                }
                result.add(txn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public BigDecimal getAverageAmount(String sender) {
        String sql = "SELECT AVG(amount) FROM transactions WHERE sender = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sender);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }


    @Override
    public int getFrequency(String sender) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE sender = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sender);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        List<Transaction> result = new ArrayList<>();
        String sql = "SELECT * FROM transactions";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Transaction txn = new Transaction(
                        rs.getString("sender"),
                        rs.getString("receiver"),
                        rs.getBigDecimal("amount"),
                        rs.getString("currency"),
                        rs.getString("country"),
                        null
                );
                if (rs.getString("risk_score") != null) {
                    txn.setRiskScore(RiskScore.valueOf(rs.getString("risk_score")));
                }
                result.add(txn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
