package com.code.algonix.problems.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatsResponse {
    private Long totalProblems;
    private Map<String, Long> categoryStats;
}