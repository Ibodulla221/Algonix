package com.code.algonix.problems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunCodeRequest {
    private Long problemId;
    private String code;
    private String language;
    private String customInput; // optional custom test case
}
