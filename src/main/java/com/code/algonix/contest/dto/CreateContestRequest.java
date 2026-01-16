package com.code.algonix.contest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateContestRequest {
    
    @NotBlank(message = "Contest number is required")
    private String number;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    private String imageUrl;
    
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    
    @NotNull(message = "Duration is required")
    private Integer durationSeconds;
    
    private String prizePool; // JSON string
    
    private List<ContestProblemRequest> problems;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContestProblemRequest {
        @NotNull(message = "Problem ID is required")
        private Long problemId;
        
        @NotBlank(message = "Symbol is required")
        private String symbol;
        
        @NotNull(message = "Points is required")
        private Integer points;
        
        @NotNull(message = "Order index is required")
        private Integer orderIndex;
    }
}
