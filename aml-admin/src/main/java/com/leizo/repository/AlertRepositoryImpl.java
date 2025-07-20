package com.leizo.repository;

import com.leizo.model.Alert;
import com.leizo.model.Transaction;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

/**
 * AlertRepositoryImpl provides the concrete implementation of the AlertRepository interface,
 * storing alerts in a PostgreSQL database and performing deduplication checks using alert fingerprints.
 *
 * This class uses Spring Boot's DataSource injection for secure and centralized database configuration.
 */
@Repository
public class AlertRepositoryImpl implements AlertRepository {

    @Autowired
    private DataSource dataSource;

    // SQL statement for inserting a new alert record into the database
    private static final String INSERT_SQL =
            "INSERT INTO alerts (alert_id, sender, receiver, amount, currency, country, reason, alert_type, priority_level, timestamp, fingerprint) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    // SQL statement for checking if an alert already exists using its fingerprint hash
    private static final String CHECK_SQL =
            "SELECT COUNT(*) FROM alerts WHERE fingerprint = ?";

    /**
     * Persists a new Alert object into the PostgreSQL database.
     * Transactional information is unpacked and inserted with proper field mapping.
     *
     * @param alert the Alert object containing the full transaction and evaluation details
     */
    @Override
    public void saveAlert(Alert alert) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            Transaction txn = alert.getTransaction();

            stmt.setString(1, alert.getAlertId());
            stmt.setString(2, txn.getSender());
            stmt.setString(3, txn.getReceiver());
            stmt.setBigDecimal(4, txn.getAmount());
            stmt.setString(5, txn.getCurrency());
            stmt.setString(6, txn.getCountry());
            stmt.setString(7, alert.getReason());
            stmt.setString(8, alert.getAlertType());
            stmt.setString(9, alert.getPriorityLevel());
            stmt.setTimestamp(10, new Timestamp(alert.getTimestamp()));
            stmt.setString(11, alert.getFingerPrint());

            stmt.executeUpdate();
            System.out.println("[DB] Alert persisted successfully.");

        } catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to save alert: " + e.getMessage());
        }
    }

    /**
     * Checks if an alert already exists in the database by verifying its fingerprint hash.
     * Prevents duplicate alert insertion for the same flagged transaction.
     *
     * @param fingerprint unique hash representing the alert identity
     * @return true if an alert with the same fingerprint already exists, false otherwise
     */
    @Override
    public boolean alertExistsByFingerPrint(String fingerprint) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_SQL)) {

            stmt.setString(1, fingerprint);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("[DB ERROR]  Failed to verify alert existence: " + e.getMessage());
        }
        return false;
    }
}
