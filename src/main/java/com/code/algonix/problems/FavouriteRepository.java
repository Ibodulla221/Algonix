package com.code.algonix.problems;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.code.algonix.user.UserEntity;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    
    // Check if user has favourited a problem
    boolean existsByUserAndProblem(UserEntity user, Problem problem);
    
    // Find favourite by user and problem
    Optional<Favourite> findByUserAndProblem(UserEntity user, Problem problem);
    
    // Get all favourites for a user
    List<Favourite> findByUserOrderByCreatedAtDesc(UserEntity user);
    
    // Get all favourites for a user with pagination
    Page<Favourite> findByUserOrderByCreatedAtDesc(UserEntity user, Pageable pageable);
    
    // Search favourites by problem title
    @Query("SELECT f FROM Favourite f JOIN f.problem p WHERE f.user = :user AND LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY f.createdAt DESC")
    Page<Favourite> findByUserAndProblemTitleContainingIgnoreCase(
        @Param("user") UserEntity user, 
        @Param("searchTerm") String searchTerm, 
        Pageable pageable
    );
    
    // Search favourites by problem title and difficulty
    @Query("SELECT f FROM Favourite f JOIN f.problem p WHERE f.user = :user AND LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND p.difficulty = :difficulty ORDER BY f.createdAt DESC")
    Page<Favourite> findByUserAndProblemTitleContainingIgnoreCaseAndDifficulty(
        @Param("user") UserEntity user,
        @Param("searchTerm") String searchTerm,
        @Param("difficulty") Problem.Difficulty difficulty,
        Pageable pageable
    );
    
    // Search favourites by problem title and categories
    @Query("SELECT DISTINCT f FROM Favourite f JOIN f.problem p WHERE f.user = :user AND LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND EXISTS (SELECT 1 FROM p.categories c WHERE c IN :categories) ORDER BY f.createdAt DESC")
    Page<Favourite> findByUserAndProblemTitleContainingIgnoreCaseAndCategories(
        @Param("user") UserEntity user,
        @Param("searchTerm") String searchTerm,
        @Param("categories") List<String> categories,
        Pageable pageable
    );
    
    // Search favourites by problem title, difficulty and categories
    @Query("SELECT DISTINCT f FROM Favourite f JOIN f.problem p WHERE f.user = :user AND LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND p.difficulty = :difficulty AND EXISTS (SELECT 1 FROM p.categories c WHERE c IN :categories) ORDER BY f.createdAt DESC")
    Page<Favourite> findByUserAndProblemTitleContainingIgnoreCaseAndDifficultyAndCategories(
        @Param("user") UserEntity user,
        @Param("searchTerm") String searchTerm,
        @Param("difficulty") Problem.Difficulty difficulty,
        @Param("categories") List<String> categories,
        Pageable pageable
    );
    
    // Filter favourites by difficulty only
    @Query("SELECT f FROM Favourite f JOIN f.problem p WHERE f.user = :user AND p.difficulty = :difficulty ORDER BY f.createdAt DESC")
    Page<Favourite> findByUserAndProblemDifficulty(
        @Param("user") UserEntity user,
        @Param("difficulty") Problem.Difficulty difficulty,
        Pageable pageable
    );
    
    // Filter favourites by categories only
    @Query("SELECT DISTINCT f FROM Favourite f JOIN f.problem p WHERE f.user = :user AND EXISTS (SELECT 1 FROM p.categories c WHERE c IN :categories) ORDER BY f.createdAt DESC")
    Page<Favourite> findByUserAndProblemCategories(
        @Param("user") UserEntity user,
        @Param("categories") List<String> categories,
        Pageable pageable
    );
    
    // Filter favourites by difficulty and categories
    @Query("SELECT DISTINCT f FROM Favourite f JOIN f.problem p WHERE f.user = :user AND p.difficulty = :difficulty AND EXISTS (SELECT 1 FROM p.categories c WHERE c IN :categories) ORDER BY f.createdAt DESC")
    Page<Favourite> findByUserAndProblemDifficultyAndCategories(
        @Param("user") UserEntity user,
        @Param("difficulty") Problem.Difficulty difficulty,
        @Param("categories") List<String> categories,
        Pageable pageable
    );
    
    // Get all favourites for a problem
    List<Favourite> findByProblem(Problem problem);
    
    // Count favourites for a problem
    long countByProblem(Problem problem);
}