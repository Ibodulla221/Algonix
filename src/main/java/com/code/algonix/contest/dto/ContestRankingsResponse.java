package com.code.algonix.contest.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContestRankingsResponse {
    private Long contestId;
    private String contestTitle;
    private String status; // ACTIVE, FINISHED, FINALIZED
    private Integer totalParticipants;
    private List<RankingEntry> rankings;
    
    @Data
    public static class RankingEntry {
        private Integer rank;
        private Long userId;
        private String username;
        private String avatarUrl;
        private Integer totalScore;
        private Integer problemsSolved;
        private Long totalPenalty;
        private Integer ratingChange; // null agar hali finalize qilinmagan bo'lsa
    }
}