package com.leizo.admin.repository;

import com.leizo.admin.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Integer> {

    // Find alerts by alert ID
    Alert findByAlertId(String alertId);

    // Find alerts by transaction ID
    List<Alert> findByTransactionId(Integer transactionId);

    // Find alerts by alert type
    List<Alert> findByAlertType(String alertType);

    // Find alerts by priority level
    List<Alert> findByPriorityLevel(String priorityLevel);

    // Find alerts by rule ID - removed due to missing ruleId field
    // List<Alert> findByRuleId(String ruleId);

    // Find high priority alerts - removed due to missing createdAt field
    // List<Alert> findByPriorityLevelOrderByCreatedAtDesc(String priorityLevel);

    // Find alerts by priority score range - removed due to missing priorityScore field
    // @Query("SELECT a FROM Alert a WHERE a.priorityScore BETWEEN :minScore AND :maxScore")
    // List<Alert> findByPriorityScoreRange(@Param("minScore") Integer minScore, 
    //                                     @Param("maxScore") Integer maxScore);

    // Find alerts by date range - removed due to missing createdAt field
    // @Query("SELECT a FROM Alert a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    // List<Alert> findByDateRange(@Param("startDate") LocalDateTime startDate, 
    //                            @Param("endDate") LocalDateTime endDate);

    // Find alerts by finger print - removed due to missing fingerPrint field
    // List<Alert> findByFingerPrint(String fingerPrint);

    // Find alerts with high priority score - removed due to missing priorityScore field
    // @Query("SELECT a FROM Alert a WHERE a.priorityScore >= 75 ORDER BY a.priorityScore DESC")
    // List<Alert> findHighPriorityAlerts();

    // Count alerts by priority level
    @Query("SELECT a.priorityLevel, COUNT(a) FROM Alert a GROUP BY a.priorityLevel")
    List<Object[]> countByPriorityLevel();

    // Count alerts by alert type
    @Query("SELECT a.alertType, COUNT(a) FROM Alert a GROUP BY a.alertType")
    List<Object[]> countByAlertType();

    // Find recent alerts (last 24 hours) - removed due to missing createdAt field
    // @Query("SELECT a FROM Alert a WHERE a.createdAt >= :since ORDER BY a.createdAt DESC")
    // List<Alert> findRecentAlerts(@Param("since") LocalDateTime since);

    // Find alerts by reason containing text
    @Query("SELECT a FROM Alert a WHERE LOWER(a.reason) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Alert> findByReasonContaining(@Param("keyword") String keyword);

    // Find alerts by timestamp range - fixed to use LocalDateTime
    @Query("SELECT a FROM Alert a WHERE a.timestamp BETWEEN :startTimestamp AND :endTimestamp")
    List<Alert> findByTimestampRange(@Param("startTimestamp") LocalDateTime startTimestamp, 
                                    @Param("endTimestamp") LocalDateTime endTimestamp);
} 