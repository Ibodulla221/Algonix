package com.code.algonix.user.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DifficultyStatsResponse {
    private Integer allProblems;
    private Integer allUserSolvedProblems;
    private List<DifficultyStatDto> difficultyStats;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DifficultyStatDto {
        private String name;
        private Integer total;
        private Integer solved;
    }
}