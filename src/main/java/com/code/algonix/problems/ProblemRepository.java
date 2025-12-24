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
}
