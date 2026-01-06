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
    
    // Admin panel uchun
    long countByStatus(Submission.SubmissionStatus status);
    long countBySubmittedAtAfter(LocalDateTime date);
    
    @Query("SELECT DATE(s.submittedAt) as date, COUNT(s) as count FROM Submission s WHERE s.submittedAt >= :startDate GROUP BY DATE(s.submittedAt) ORDER BY DATE(s.submittedAt)")
    List<Object[]> findDailySubmissionStats(@Param("startDate") LocalDateTime startDate);
}
