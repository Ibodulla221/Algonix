package com.code.algonix.contest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestSubmissionRepository extends JpaRepository<ContestSubmission, Long> {
    
    List<ContestSubmission> findByContestIdAndUserIdOrderBySubmittedAtDesc(Long contestId, Long userId);
    
    List<ContestSubmission> findByContestIdAndContestProblemId(Long contestId, Long contestProblemId);
    
    @Query("SELECT cs FROM ContestSubmission cs WHERE cs.contest.id = :contestId " +
           "AND cs.user.id = :userId AND cs.contestProblem.id = :contestProblemId " +
           "ORDER BY cs.submittedAt DESC")
    List<ContestSubmission> findUserProblemSubmissions(Long contestId, Long userId, Long contestProblemId);
    
    boolean existsByContestIdAndUserIdAndContestProblemIdAndIsAcceptedTrue(
            Long contestId, Long userId, Long contestProblemId);
}
