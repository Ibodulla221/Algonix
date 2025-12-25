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
}
