package com.code.algonix.contest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContestResponse {
    private String id;
    private String number;
    private String title;
    private String description;
    private String imageUrl;
    private LocalDateTime startTime;
    private Integer durationSeconds;
    private Integer problemCount;
    private Integer participantsCount;
    private List<Object> prizePool;
    private String status; // upcoming, active, finished, registered
}
