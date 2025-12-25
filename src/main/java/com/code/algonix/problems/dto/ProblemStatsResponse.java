package com.code.algonix.problems.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemStatsResponse {
    private Long allProblems;
    private Long allUserSolvedProblems;
    private List<DifficultyStatItem> difficultyStats;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DifficultyStatItem {
        private String name;
        private Long total;
        private Long solved;
    }
}