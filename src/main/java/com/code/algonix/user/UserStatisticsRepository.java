package com.code.algonix.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatistics, Long> {
    Optional<UserStatistics> findByUserId(Long userId);
    Optional<UserStatistics> findByUser(UserEntity user);
    
    // Admin panel uchun
    @Query("SELECT u.username, us.totalSolved FROM UserStatistics us JOIN us.user u ORDER BY us.totalSolved DESC")
    List<Object[]> findTopUsersByTotalSolved(Pageable pageable);
}