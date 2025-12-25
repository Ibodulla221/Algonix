package com.code.algonix.problems;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
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
    
    // Get all favourites for a problem
    List<Favourite> findByProblem(Problem problem);
    
    // Count favourites for a problem
    long countByProblem(Problem problem);
}