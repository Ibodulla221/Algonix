package com.code.algonix.contest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContestStandingsResponse {
    private Integer rank;
    private Long userId;
    private String username;
    private String avatarUrl;
    private Integer score; // 100 ballik sistemada
    private Integer problemsSolved;
    private Long totalPenalty;
    private Integer ratingChange; // contest yakunida berilgan rating
    private Integer totalRating; // barcha contestlardagi umumiy rating
    private List<ProblemResult> problems;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProblemResult {
        private String symbol;
        private Boolean solved;
        private Integer attempts;
        private Integer score;
        private Long timeTaken;
    }
}
