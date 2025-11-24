package com.code.algonix.problems.dto;

import com.code.algonix.problems.Problem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
        private Long id;
        private String slug;
        private String title;
        private Problem.Difficulty difficulty;
        private Double acceptanceRate;
        private Boolean isPremium;
        private String status; // solved, attempted, todo
        private Double frequency;
        private List<String> categories;
    }
}
