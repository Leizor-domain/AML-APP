package com.leizo.common.repository;

import com.leizo.common.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    Optional<Users> findById(Integer id);
    boolean existsByUsername(String username);
    // Add missing methods for admin usage
    Page<Users> findByEnabled(Boolean enabled, Pageable pageable);
    Page<Users> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    @Query("SELECT u.role, COUNT(u) FROM Users u GROUP BY u.role")
    List<Object[]> countUsersByRole();
} 