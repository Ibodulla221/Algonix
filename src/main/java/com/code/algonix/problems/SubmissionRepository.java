package com.code.algonix.problems;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.code.algonix.user.UserEntity;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByUserIdOrderBySubmittedAtDesc(Long userId);
    List<Submission> findByProblemIdOrderBySubmittedAtDesc(Long problemId);
    List<Submission> findByUserIdAndProblemId(Long userId, Long problemId);
    
    // Gamification support
    boolean existsByUserAndProblemAndStatus(UserEntity user, Problem problem, Submission.SubmissionStatus status);
    boolean existsByUserAndProblemAndStatusAndIdNot(UserEntity user, Problem problem, Submission.SubmissionStatus status, Long excludeId);
    
    // User statistics
    @Query("SELECT COUNT(DISTINCT s.problem) FROM Submission s WHERE s.user = :user AND s.status = 'ACCEPTED'")
    Long countSolvedProblemsByUser(@Param("user") UserEntity user);
    
    @Query("SELECT COUNT(DISTINCT s.problem) FROM Submission s WHERE s.user = :user AND s.status = 'ACCEPTED' AND s.problem.difficulty = :difficulty")
    Long countSolvedProblemsByUserAndDifficulty(@Param("user") UserEntity user, @Param("difficulty") Problem.Difficulty difficulty);
    
    @Query("SELECT COUNT(DISTINCT s.problem) FROM Submission s WHERE s.user = :user AND s.status = 'ACCEPTED' AND EXISTS (SELECT 1 FROM s.problem.categories c WHERE c = :category)")
    Long countSolvedProblemsByUserAndCategory(@Param("user") UserEntity user, @Param("category") String category);
    
    // Admin panel uchun
    long countByStatus(Submission.SubmissionStatus status);
    long countBySubmittedAtAfter(LocalDateTime date);
    
    @Query("SELECT DATE(s.submittedAt) as date, COUNT(s) as count FROM Submission s WHERE s.submittedAt >= :startDate GROUP BY DATE(s.submittedAt) ORDER BY DATE(s.submittedAt)")
    List<Object[]> findDailySubmissionStats(@Param("startDate") LocalDateTime startDate);
    
    // User profile uchun - kunlik yechilgan masalalar
    @Query("SELECT EXTRACT(DAY FROM s.submittedAt) as day, COUNT(DISTINCT s.problem) FROM Submission s WHERE s.user = :user AND s.status = 'ACCEPTED' AND s.submittedAt >= :startDate AND s.submittedAt < :endDate GROUP BY EXTRACT(DAY FROM s.submittedAt) ORDER BY day")
    List<Object[]> findDailySolvedProblemsByUserAndMonth(@Param("user") UserEntity user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Admin panel uchun - oylik yechilgan masalalar statistikasi
    @Query("SELECT EXTRACT(MONTH FROM s.submittedAt) as month, COUNT(DISTINCT s.problem) FROM Submission s WHERE s.status = 'ACCEPTED' AND EXTRACT(YEAR FROM s.submittedAt) = :year GROUP BY EXTRACT(MONTH FROM s.submittedAt) ORDER BY month")
    List<Object[]> findMonthlySolvedProblemsByYear(@Param("year") Integer year);
    
    // Admin panel uchun - kunlik yechilgan masalalar statistikasi
    @Query("SELECT EXTRACT(DAY FROM s.submittedAt) as day, COUNT(DISTINCT s.problem) FROM Submission s WHERE s.status = 'ACCEPTED' AND EXTRACT(YEAR FROM s.submittedAt) = :year AND EXTRACT(MONTH FROM s.submittedAt) = :month GROUP BY EXTRACT(DAY FROM s.submittedAt) ORDER BY day")
    List<Object[]> findDailySolvedProblemsByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);
    
    // Mavjud yillarni olish
    @Query("SELECT DISTINCT EXTRACT(YEAR FROM s.submittedAt) as year FROM Submission s WHERE s.status = 'ACCEPTED' AND s.submittedAt IS NOT NULL ORDER BY year DESC")
    List<Integer> findAvailableSubmissionYears();
}
