package com.code.algonix.user.profile.dto;

import com.code.algonix.user.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long userId;
    private String username;
    private String email;
    private Role role;
    private String avatarUrl;
    private Statistics statistics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Statistics {
        private Integer totalSolved;
        private Integer beginnerSolved;
        private Integer basicSolved;
        private Integer normalSolved;
        private Integer mediumSolved;
        private Integer hardSolved;
        private Double acceptanceRate;
        private Integer ranking;
        private Integer reputation;
        private Integer streakDays;
    }
}
