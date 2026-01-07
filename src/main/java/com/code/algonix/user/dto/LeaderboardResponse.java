package com.code.algonix.user.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardResponse {
    private List<UserRankingDto> users;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private String sortBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRankingDto {
        private Long userId;
        private String username;
        private String email;
        private String location; // Mamlakat/shahar
        private Integer ranking;
        private Integer totalSolved;
        private Integer beginnerSolved;
        private Integer basicSolved;
        private Integer normalSolved;
        private Integer mediumSolved;
        private Integer hardSolved;
        private Double acceptanceRate;
        private Integer experience;
        private Integer level;
        private Integer currentLevelXp;
        private Integer coins;
        private Integer currentStreak;
        private Integer longestStreak;
        private LocalDate lastLoginDate;
        private String status; // "online", "offline", "away"
        private String badge; // "Beginner", "Expert", "Master", etc.
    }
}