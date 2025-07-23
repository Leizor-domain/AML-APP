package com.leizo.admin.repository;

import com.leizo.admin.auth.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    
    // Find user by username
    Optional<Users> findByUsername(String username);
    
    // Find users by role
    List<Users> findByRole(String role);
    
    // Find users created after a specific date
    List<Users> findByCreatedAtAfter(LocalDateTime date);
    
    // Find users by username containing text (for search)
    @Query("SELECT u FROM Users u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Users> findByUsernameContaining(@Param("keyword") String keyword);
    
    // Count users by role
    @Query("SELECT u.role, COUNT(u) FROM Users u GROUP BY u.role")
    List<Object[]> countByRole();
    
    // Find recent users (created in last 30 days)
    @Query("SELECT u FROM Users u WHERE u.createdAt >= :since ORDER BY u.createdAt DESC")
    List<Users> findRecentUsers(@Param("since") LocalDateTime since);
    
    // Check if username exists
    boolean existsByUsername(String username);
    
    // Find users updated after a specific date
    List<Users> findByUpdatedAtAfter(LocalDateTime date);

    Page<Users> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    Page<Users> findByEnabled(boolean enabled, Pageable pageable);

    @Query("SELECT u.role, COUNT(u) FROM Users u GROUP BY u.role")
    List<Object[]> countUsersByRole();
} 