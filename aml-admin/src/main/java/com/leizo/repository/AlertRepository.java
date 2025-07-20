package com.leizo.repository;


import com.leizo.model.Alert;

/**
 * AlertRepository defines the contract for managing alert data within the AML system.
 * This abstraction allows for multiple persistence implementations (e.g., in-memory, file-based, PostgreSQL).
 */
public interface AlertRepository {

    /**
     * Persists an alert to the underlying storage mechanism.
     *
     * @param alert the Alert object containing flagged transaction details
     */
    void saveAlert(Alert alert);

    /**
     * Checks if an alert already exists by comparing a unique fingerprint hash.
     * This is used to prevent duplicate alerts for the same transaction signature.
     *
     * @param fingerprint a hash or identifier representing the alert's uniqueness
     * @return true if an alert with the given fingerprint exists; false otherwise
     */
    boolean alertExistsByFingerPrint(String fingerprint);

}
