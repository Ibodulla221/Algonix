package com.code.algonix.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    // Basic info
    private Long id;
    private String username;
    private String email;
    private Role role;
    
    // Profile info
    private String firstName;
    private String lastName;
    private String fullName;
    private String displayName;
    private String bio;
    private String location;
    private String company;
    private String jobTitle;
    private String website;
    private String githubUsername;
    private String linkedinUrl;
    private String twitterUsername;
    
    // Avatar
    private String avatarUrl;
    
    // Settings (only for own profile)
    private Boolean isProfilePublic;
    private Boolean showEmail;
    private Boolean showLocation;
    private Boolean showCompany;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Statistics
    private UserStatisticsDto statistics;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStatisticsDto {
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
        private Integer coins;
        private Integer experience;
        private Integer level;
        private Integer currentLevelXp;
        private Integer currentStreak;
        private Integer longestStreak;
        private Integer weeklyStreak;
        private Integer monthlyStreak;
        private LocalDate lastLoginDate;
    }
}