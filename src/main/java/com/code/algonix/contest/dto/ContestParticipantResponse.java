package com.code.algonix.contest.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContestParticipantResponse {
    private Long id;
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private LocalDateTime registeredAt;
    private Integer score;
    private Integer rank;
    private Integer ratingChange;
    private Integer problemsSolved;
    private Long totalPenalty;
    private Integer totalRating; // Barcha contest'lardan jami rating
}