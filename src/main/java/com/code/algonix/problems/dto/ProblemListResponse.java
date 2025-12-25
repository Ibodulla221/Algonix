package com.code.algonix.problems.dto;

import java.util.List;

import com.code.algonix.problems.Problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemListResponse {
    private Long total;
    private Integer page;
    private Integer pageSize;
    private List<ProblemSummary> problems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProblemSummary {
        private Integer sequenceNumber; // Tartib raqami
        private Long id;
        private String slug;
        private String title;
        private Problem.Difficulty difficulty;
        private Double acceptanceRate;
        private Boolean isPremium;
        private String status; // solved, attempted, todo
        private Boolean isFavourite; // Is favourited by user
        private Double frequency;
        private List<String> categories;
        private Integer timeLimitMs; // Time limit in milliseconds
        private Integer memoryLimitMb; // Memory limit in MB
    }
}
