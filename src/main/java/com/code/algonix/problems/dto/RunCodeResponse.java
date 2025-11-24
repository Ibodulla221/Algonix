package com.code.algonix.problems.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunCodeResponse {
    private String status; // success, error
    private String output;
    private String expectedOutput;
    private Integer runtime;
    private Double memory;
    private String errorMessage;
    private Boolean passed;
}
