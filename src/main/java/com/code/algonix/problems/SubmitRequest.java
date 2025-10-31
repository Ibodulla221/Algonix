package com.code.algonix.problems;

import lombok.Data;

@Data
public class SubmitRequest {
    private Long userId;
    private Long problemId;
    private String language;
    private String code;
}
