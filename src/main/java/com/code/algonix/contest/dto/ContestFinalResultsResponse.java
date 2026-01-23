package com.code.algonix.contest.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ContestFinalResultsResponse {
    private Long contestId;
    private String contestTitle;
    private LocalDateTime finalizedAt;
    private Integer totalParticipants;
    private List<FinalParticipantResult> results;
    
    @Data
    public static class FinalParticipantResult {
        private Integer rank;
        private Long userId;
        private String username;
        private String firstName;
        private String lastName;
        private String avatarUrl;
        private Integer totalScore;
        private Integer problemsSolved;
        private Long totalPenalty;
        private Integer ratingChange;
        private Integer newTotalRating;
        private List<ProblemResult> problemResults;
    }
    
    @Data
    public static class ProblemResult {
        private String problemSymbol;
        private String problemTitle;
        private Integer points;
        private Boolean solved;
        private Integer attempts;
        private Long timeTaken; // sekundlarda
        private Integer penalty; // noto'g'ri urinishlar uchun
    }
}