package com.code.algonix.problems;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    Optional<Problem> findBySlug(String slug);
    boolean existsBySlug(String slug);
    
    // Filter by difficulty only
    Page<Problem> findByDifficulty(Problem.Difficulty difficulty, Pageable pageable);
    
    // Filter by categories only (contains any of the given categories)
    @Query("SELECT DISTINCT p FROM Problem p WHERE EXISTS (SELECT 1 FROM p.categories c WHERE c IN :categories)")
    Page<Problem> findByCategories(@Param("categories") List<String> categories, Pageable pageable);
    
    // Filter by both difficulty and categories
    @Query("SELECT DISTINCT p FROM Problem p WHERE p.difficulty = :difficulty AND EXISTS (SELECT 1 FROM p.categories c WHERE c IN :categories)")
    Page<Problem> findByDifficultyAndCategories(
        @Param("difficulty") Problem.Difficulty difficulty,
        @Param("categories") List<String> categories,
        Pageable pageable
    );
    
    // Statistics methods
    @Query("SELECT COUNT(p) FROM Problem p WHERE p.difficulty = :difficulty")
    Long countByDifficulty(@Param("difficulty") Problem.Difficulty difficulty);
    
    // Category statistics
    @Query("SELECT c, COUNT(DISTINCT p) FROM Problem p JOIN p.categories c GROUP BY c")
    List<Object[]> countByCategory();
    
    // Search methods
    // Search by title only
    @Query("SELECT p FROM Problem p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Problem> findByTitleContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Search by global sequence number
    Page<Problem> findByGlobalSequenceNumber(Integer globalSequenceNumber, Pageable pageable);
    
    // Search by title with difficulty filter
    @Query("SELECT p FROM Problem p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND p.difficulty = :difficulty")
    Page<Problem> findByTitleContainingIgnoreCaseAndDifficulty(
        @Param("searchTerm") String searchTerm, 
        @Param("difficulty") Problem.Difficulty difficulty, 
        Pageable pageable
    );
    
    // Search by title with categories filter
    @Query("SELECT DISTINCT p FROM Problem p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND EXISTS (SELECT 1 FROM p.categories c WHERE c IN :categories)")
    Page<Problem> findByTitleContainingIgnoreCaseAndCategories(
        @Param("searchTerm") String searchTerm,
        @Param("categories") List<String> categories,
        Pageable pageable
    );
    
    // Search by title with both difficulty and categories filter
    @Query("SELECT DISTINCT p FROM Problem p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND p.difficulty = :difficulty AND EXISTS (SELECT 1 FROM p.categories c WHERE c IN :categories)")
    Page<Problem> findByTitleContainingIgnoreCaseAndDifficultyAndCategories(
        @Param("searchTerm") String searchTerm,
        @Param("difficulty") Problem.Difficulty difficulty,
        @Param("categories") List<String> categories,
        Pageable pageable
    );
    
    // Admin statistics methods
    // Monthly problem creation statistics
    @Query("SELECT EXTRACT(YEAR FROM p.createdAt) as year, EXTRACT(MONTH FROM p.createdAt) as month, COUNT(p) " +
           "FROM Problem p " +
           "WHERE p.createdAt IS NOT NULL " +
           "GROUP BY EXTRACT(YEAR FROM p.createdAt), EXTRACT(MONTH FROM p.createdAt) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> findMonthlyProblemCreationStats();
    
    // Yearly problem creation statistics
    @Query("SELECT EXTRACT(YEAR FROM p.createdAt) as year, COUNT(p) " +
           "FROM Problem p " +
           "WHERE p.createdAt IS NOT NULL " +
           "GROUP BY EXTRACT(YEAR FROM p.createdAt) " +
           "ORDER BY year DESC")
    List<Object[]> findYearlyProblemCreationStats();
    
    // Monthly problem creation statistics for specific year
    @Query("SELECT EXTRACT(MONTH FROM p.createdAt) as month, COUNT(p) " +
           "FROM Problem p " +
           "WHERE EXTRACT(YEAR FROM p.createdAt) = :year AND p.createdAt IS NOT NULL " +
           "GROUP BY EXTRACT(MONTH FROM p.createdAt) " +
           "ORDER BY month")
    List<Object[]> findMonthlyProblemCreationStatsByYear(@Param("year") Integer year);
    
    // Problem creation statistics by difficulty and year
    @Query("SELECT p.difficulty, EXTRACT(MONTH FROM p.createdAt) as month, COUNT(p) " +
           "FROM Problem p " +
           "WHERE EXTRACT(YEAR FROM p.createdAt) = :year AND p.createdAt IS NOT NULL " +
           "GROUP BY p.difficulty, EXTRACT(MONTH FROM p.createdAt) " +
           "ORDER BY month, p.difficulty")
    List<Object[]> findProblemCreationStatsByDifficultyAndYear(@Param("year") Integer year);
    
    // Total problems created by year
    @Query("SELECT COUNT(p) FROM Problem p WHERE EXTRACT(YEAR FROM p.createdAt) = :year AND p.createdAt IS NOT NULL")
    Long countProblemsByYear(@Param("year") Integer year);
    
    // Get available years for filtering
    @Query("SELECT DISTINCT EXTRACT(YEAR FROM p.createdAt) as year " +
           "FROM Problem p " +
           "WHERE p.createdAt IS NOT NULL " +
           "ORDER BY year DESC")
    List<Integer> findAvailableYears();
    
    // Daily problem creation statistics for specific month and year
    @Query("SELECT EXTRACT(DAY FROM p.createdAt) as day, COUNT(p) " +
           "FROM Problem p " +
           "WHERE EXTRACT(YEAR FROM p.createdAt) = :year " +
           "AND EXTRACT(MONTH FROM p.createdAt) = :month " +
           "AND p.createdAt IS NOT NULL " +
           "GROUP BY EXTRACT(DAY FROM p.createdAt) " +
           "ORDER BY day")
    List<Object[]> findDailyProblemCreationStatsByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);
    
    // Contest-specific methods
    // Find only public problems (not contest-only or published contest problems)
    @Query("SELECT p FROM Problem p WHERE (p.isContestOnly = false OR p.isContestOnly IS NULL) OR (p.isContestOnly = true AND p.publishTime IS NOT NULL AND p.publishTime <= CURRENT_TIMESTAMP)")
    Page<Problem> findPublicProblems(Pageable pageable);
    
    // Find public problems by difficulty
    @Query("SELECT p FROM Problem p WHERE p.difficulty = :difficulty AND ((p.isContestOnly = false OR p.isContestOnly IS NULL) OR (p.isContestOnly = true AND p.publishTime IS NOT NULL AND p.publishTime <= CURRENT_TIMESTAMP))")
    Page<Problem> findPublicProblemsByDifficulty(@Param("difficulty") Problem.Difficulty difficulty, Pageable pageable);
    
    // Find public problems by categories
    @Query("SELECT DISTINCT p FROM Problem p WHERE EXISTS (SELECT 1 FROM p.categories c WHERE c IN :categories) AND ((p.isContestOnly = false OR p.isContestOnly IS NULL) OR (p.isContestOnly = true AND p.publishTime IS NOT NULL AND p.publishTime <= CURRENT_TIMESTAMP))")
    Page<Problem> findPublicProblemsByCategories(@Param("categories") List<String> categories, Pageable pageable);
    
    // Find public problems by difficulty and categories
    @Query("SELECT DISTINCT p FROM Problem p WHERE p.difficulty = :difficulty AND EXISTS (SELECT 1 FROM p.categories c WHERE c IN :categories) AND ((p.isContestOnly = false OR p.isContestOnly IS NULL) OR (p.isContestOnly = true AND p.publishTime IS NOT NULL AND p.publishTime <= CURRENT_TIMESTAMP))")
    Page<Problem> findPublicProblemsByDifficultyAndCategories(
        @Param("difficulty") Problem.Difficulty difficulty,
        @Param("categories") List<String> categories,
        Pageable pageable
    );
    
    // Find contest-only problems for a specific contest
    // contestId ni olib tashladik - endi ContestProblem orqali bog'lanadi
    @Query("SELECT cp.problem FROM ContestProblem cp WHERE cp.contest.id = :contestId AND cp.problem.isContestOnly = true")
    List<Problem> findContestOnlyProblemsByContestId(@Param("contestId") Long contestId);
    
    // Find public problems by title search
    @Query("SELECT p FROM Problem p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND ((p.isContestOnly = false OR p.isContestOnly IS NULL) OR (p.isContestOnly = true AND p.publishTime IS NOT NULL AND p.publishTime <= CURRENT_TIMESTAMP))")
    Page<Problem> findPublicProblemsByTitleContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Find public problems by title and difficulty
    @Query("SELECT p FROM Problem p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND p.difficulty = :difficulty AND ((p.isContestOnly = false OR p.isContestOnly IS NULL) OR (p.isContestOnly = true AND p.publishTime IS NOT NULL AND p.publishTime <= CURRENT_TIMESTAMP))")
    Page<Problem> findPublicProblemsByTitleContainingIgnoreCaseAndDifficulty(
        @Param("searchTerm") String searchTerm, 
        @Param("difficulty") Problem.Difficulty difficulty, 
        Pageable pageable
    );
    
    // Find public problems by title and categories
    @Query("SELECT DISTINCT p FROM Problem p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND EXISTS (SELECT 1 FROM p.categories c WHERE c IN :categories) AND ((p.isContestOnly = false OR p.isContestOnly IS NULL) OR (p.isContestOnly = true AND p.publishTime IS NOT NULL AND p.publishTime <= CURRENT_TIMESTAMP))")
    Page<Problem> findPublicProblemsByTitleContainingIgnoreCaseAndCategories(
        @Param("searchTerm") String searchTerm,
        @Param("categories") List<String> categories,
        Pageable pageable
    );
    
    // Find public problems by title, difficulty and categories
    @Query("SELECT DISTINCT p FROM Problem p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND p.difficulty = :difficulty AND EXISTS (SELECT 1 FROM p.categories c WHERE c IN :categories) AND ((p.isContestOnly = false OR p.isContestOnly IS NULL) OR (p.isContestOnly = true AND p.publishTime IS NOT NULL AND p.publishTime <= CURRENT_TIMESTAMP))")
    Page<Problem> findPublicProblemsByTitleContainingIgnoreCaseAndDifficultyAndCategories(
        @Param("searchTerm") String searchTerm,
        @Param("difficulty") Problem.Difficulty difficulty,
        @Param("categories") List<String> categories,
        Pageable pageable
    );
}
