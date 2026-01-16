package com.code.algonix.contest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContestProblemResponse {
    private Long id;
    private Long problemId;
    private String problemTitle;
    private String symbol;
    private Integer ball;
    private Integer attemptsCount;
    private Integer solved;
    private Integer unsolved;
    private Integer attemptUsersCount;
    private Boolean isSolved;
    private Boolean isAttempted;
    private Double delta;
}
