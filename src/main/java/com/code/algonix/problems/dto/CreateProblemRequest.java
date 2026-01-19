package com.code.algonix.problems.dto;

import java.util.List;
import java.util.Map;

import com.code.algonix.problems.Problem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProblemRequest {
    private String slug;
    private String title;
    private Problem.Difficulty difficulty;
    private List<String> categories;
    private List<String> tags;
    private String description;
    private String descriptionHtml;
    private List<ExampleRequest> examples;
    private List<String> constraints;
    private List<String> hints;
    private Map<String, String> codeTemplates;
    private List<TestCaseRequest> testCases;
    private List<Long> relatedProblems;
    private List<String> companies;
    private Double frequency;
    private Boolean isPremium;
    
    // Contest-specific fields
    private Boolean isContestOnly = false; // Masala faqat contest uchunmi
    // contestId ni olib tashladik - contest yaratilgandan keyin bog'lanadi

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExampleRequest {
        private String caseNumber; // "1", "2", "3", etc.
        private String input;
        private String target;
        private String output;
        private String explanation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCaseRequest {
        private String input;
        private String expectedOutput;
        private Boolean isHidden;
        private Integer timeLimitMs;
    }
}
