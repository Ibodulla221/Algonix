package com.code.algonix.problems.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.code.algonix.problems.Problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDetailResponse {
    private Long id;
    private String slug;
    private String title;
    private Problem.Difficulty difficulty;
    private List<String> categories;
    private List<String> tags;
    private Integer likes;
    private Integer dislikes;
    private Double acceptanceRate;
    private Long totalSubmissions;
    private Long totalAccepted;
    private String description;
    private String descriptionHtml;
    private List<ExampleDto> examples;
    private List<String> constraints;
    private List<String> hints;
    private Map<String, String> codeTemplates; // language -> code
    private List<Long> relatedProblems;
    private List<String> companies;
    private Double frequency;
    private Boolean isPremium;
    private Integer timeLimitMs; // Time limit in milliseconds
    private Integer memoryLimitMb; // Memory limit in MB
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExampleDto {
        private Long id;
        private String caseNumber;
        private String input;
        private String target;
        private String output;
        private String explanation;
    }
}
