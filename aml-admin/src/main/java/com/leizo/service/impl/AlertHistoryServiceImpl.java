package com.leizo.service.impl;

import com.leizo.model.Alert;
import com.leizo.model.Transaction;
import com.leizo.service.AlertHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AlertHistoryServiceImpl handles saving and retrieving alert history records
 * from a PostgreSQL database.
 *
 * This implementation provides two main operations:
 * - Persisting alert information for audit or analysis.
 * - Fetching all historical alerts for review.
 *
 * All data operations use JDBC and are compatible with PostgreSQL.
 */
@Service
public class AlertHistoryServiceImpl implements AlertHistoryService {

    @Value("${spring.datasource.url}")
    private String URL;

    @Value("${spring.datasource.username}")
    private String USER;

    @Value("${spring.datasource.password}")
    private String PASSWORD;

    /**
     * Persists an alert into the alerts table using JDBC.
     *
     * @param alert the alert object containing transaction details and alert metadata
     */

    @Override
    public void saveAlert(Alert alert) {
        String sql = "INSERT INTO alerts (alert_id, sender, receiver, reason, priority_score, priority_level, alert_type) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, alert.getAlertId());
            ps.setString(2, alert.getTransaction().getSender());
            ps.setString(3, alert.getTransaction().getReceiver());
            ps.setString(4, alert.getReason());
            ps.setInt(5, alert.getPriorityScore());
            ps.setString(6, alert.getPriorityLevel());
            ps.setString(7, alert.getAlertType());

            ps.executeUpdate();
            System.out.println("[DB] Alert saved to history: " + alert.getAlertId());

        } catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to save alert: " + e.getMessage());
        }
    }


        /**
         * Retrieves all alert history records from the alerts table.
         *
         * @return an unmodifiable list of all alerts in the database
         */
        @Override
        public List<Alert> getAllAlerts() {
            List<Alert> result = new ArrayList<>();
            String sql = "SELECT * FROM alerts";

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Transaction txn = new Transaction(
                            rs.getString("sender"),
                            rs.getString("receiver"),
                            null, null, null, null
                    );

                    Alert alert = new Alert();
                    alert.setAlertId(rs.getString("alert_id"));
                    alert.setTransaction(txn);
                    alert.setReason(rs.getString("reason"));
                    alert.setPriorityScore(rs.getInt("priority_score"));
                    alert.setPriorityLevel(rs.getString("priority_level"));
                    alert.setAlertType(rs.getString("alert_type"));

                    result.add(alert);
                }

            } catch (SQLException e) {
                System.err.println("[DB ERROR] Failed to load alert history: " + e.getMessage());
            }

            return Collections.unmodifiableList(result);
        }
    }
