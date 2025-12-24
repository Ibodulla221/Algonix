package com.code.algonix.problems.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemStatsResponse {
    private Long totalProblems;
    private DifficultyStats difficultyStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DifficultyStats {
        private Long beginner;
        private Long basic;
        private Long normal;
        private Long medium;
        private Long hard;
    }
}