package com.code.algonix.contest;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestParticipantRepository extends JpaRepository<ContestParticipant, Long> {
    
    Optional<ContestParticipant> findByContestIdAndUserId(Long contestId, Long userId);
    
    boolean existsByContestIdAndUserId(Long contestId, Long userId);
    
    @Query("SELECT cp FROM ContestParticipant cp WHERE cp.contest.id = :contestId " +
           "ORDER BY cp.score DESC, cp.totalPenalty ASC, cp.problemsSolved DESC")
    List<ContestParticipant> findContestStandings(Long contestId);
    
    @Query("SELECT cp FROM ContestParticipant cp WHERE cp.user.id = :userId " +
           "ORDER BY cp.contest.startTime DESC")
    List<ContestParticipant> findUserContestHistory(Long userId);
    
    @Query("SELECT cp FROM ContestParticipant cp WHERE cp.contest.id = :contestId " +
           "ORDER BY cp.registeredAt ASC")
    List<ContestParticipant> findByContestIdOrderByRegisteredAtAsc(Long contestId);
    
    Long countByContestId(Long contestId);
}
