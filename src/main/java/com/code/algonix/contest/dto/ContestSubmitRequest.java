package com.code.algonix.contest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContestSubmitRequest {
    
    @NotNull(message = "Contest ID is required")
    private Long contestId;
    
    @NotNull(message = "Problem ID is required")
    private Long problemId;
    
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotBlank(message = "Language is required")
    private String language;
}
