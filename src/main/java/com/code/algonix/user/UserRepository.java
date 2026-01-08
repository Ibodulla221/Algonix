package com.code.algonix.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    
    // Admin panel uchun
    long countByRole(Role role);
    
    // User registration statistics
    // Monthly user registration statistics for specific year
    @Query("SELECT EXTRACT(MONTH FROM u.createdAt) as month, COUNT(u) " +
           "FROM UserEntity u " +
           "WHERE EXTRACT(YEAR FROM u.createdAt) = :year AND u.createdAt IS NOT NULL " +
           "GROUP BY EXTRACT(MONTH FROM u.createdAt) " +
           "ORDER BY month")
    List<Object[]> findMonthlyUserRegistrationStatsByYear(@Param("year") Integer year);
    
    // Yearly user registration statistics
    @Query("SELECT EXTRACT(YEAR FROM u.createdAt) as year, COUNT(u) " +
           "FROM UserEntity u " +
           "WHERE u.createdAt IS NOT NULL " +
           "GROUP BY EXTRACT(YEAR FROM u.createdAt) " +
           "ORDER BY year DESC")
    List<Object[]> findYearlyUserRegistrationStats();
    
    // Total users registered by year
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE EXTRACT(YEAR FROM u.createdAt) = :year AND u.createdAt IS NOT NULL")
    Long countUsersByYear(@Param("year") Integer year);
    
    // Get available years for user registration filtering
    @Query("SELECT DISTINCT EXTRACT(YEAR FROM u.createdAt) as year " +
           "FROM UserEntity u " +
           "WHERE u.createdAt IS NOT NULL " +
           "ORDER BY year DESC")
    List<Integer> findAvailableRegistrationYears();
    
    // User registration statistics by role and year
    @Query("SELECT u.role, EXTRACT(MONTH FROM u.createdAt) as month, COUNT(u) " +
           "FROM UserEntity u " +
           "WHERE EXTRACT(YEAR FROM u.createdAt) = :year AND u.createdAt IS NOT NULL " +
           "GROUP BY u.role, EXTRACT(MONTH FROM u.createdAt) " +
           "ORDER BY month, u.role")
    List<Object[]> findUserRegistrationStatsByRoleAndYear(@Param("year") Integer year);
}
