package com.code.algonix.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatistics, Long> {
    Optional<UserStatistics> findByUserId(Long userId);
    Optional<UserStatistics> findByUser(UserEntity user);
    
    // Admin panel uchun
    @Query("SELECT u.username, us.totalSolved FROM UserStatistics us JOIN us.user u ORDER BY us.totalSolved DESC")
    List<Object[]> findTopUsersByTotalSolved(Pageable pageable);
    
    // Leaderboard uchun metodlar
    // Top users by total solved problems
    @Query("SELECT us FROM UserStatistics us JOIN FETCH us.user u WHERE u.role = 'USER' ORDER BY us.totalSolved DESC, us.acceptanceRate DESC")
    Page<UserStatistics> findTopUsersByTotalSolvedWithPagination(Pageable pageable);
    
    // Top users by experience points
    @Query("SELECT us FROM UserStatistics us JOIN FETCH us.user u WHERE u.role = 'USER' ORDER BY us.experience DESC, us.totalSolved DESC")
    Page<UserStatistics> findTopUsersByExperience(Pageable pageable);
    
    // Top users by level
    @Query("SELECT us FROM UserStatistics us JOIN FETCH us.user u WHERE u.role = 'USER' ORDER BY us.level DESC, us.currentLevelXp DESC, us.totalSolved DESC")
    Page<UserStatistics> findTopUsersByLevel(Pageable pageable);
    
    // Top users by current streak
    @Query("SELECT us FROM UserStatistics us JOIN FETCH us.user u WHERE u.role = 'USER' ORDER BY us.currentStreak DESC, us.totalSolved DESC")
    Page<UserStatistics> findTopUsersByCurrentStreak(Pageable pageable);
    
    // Top users by longest streak
    @Query("SELECT us FROM UserStatistics us JOIN FETCH us.user u WHERE u.role = 'USER' ORDER BY us.longestStreak DESC, us.currentStreak DESC, us.totalSolved DESC")
    Page<UserStatistics> findTopUsersByLongestStreak(Pageable pageable);
    
    // Search users by username
    @Query("SELECT us FROM UserStatistics us JOIN FETCH us.user u WHERE u.role = 'USER' AND LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')) ORDER BY us.totalSolved DESC")
    Page<UserStatistics> findUsersByUsernameContaining(@Param("username") String username, Pageable pageable);
    
    // Get user ranking by total solved
    @Query("SELECT COUNT(us) + 1 FROM UserStatistics us WHERE us.totalSolved > :totalSolved OR (us.totalSolved = :totalSolved AND us.acceptanceRate > :acceptanceRate)")
    Long getUserRankingByTotalSolved(@Param("totalSolved") Integer totalSolved, @Param("acceptanceRate") Double acceptanceRate);
    
    // Get user ranking by experience
    @Query("SELECT COUNT(us) + 1 FROM UserStatistics us WHERE us.experience > :experience OR (us.experience = :experience AND us.totalSolved > :totalSolved)")
    Long getUserRankingByExperience(@Param("experience") Integer experience, @Param("totalSolved") Integer totalSolved);
}