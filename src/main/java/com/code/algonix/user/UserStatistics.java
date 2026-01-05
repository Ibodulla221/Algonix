package com.code.algonix.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "user_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;

    @Builder.Default
    private Integer totalSolved = 0;
    @Builder.Default
    private Integer beginnerSolved = 0;
    @Builder.Default
    private Integer basicSolved = 0;
    @Builder.Default
    private Integer normalSolved = 0;
    @Builder.Default
    private Integer mediumSolved = 0;
    @Builder.Default
    private Integer hardSolved = 0;
    @Builder.Default
    private Double acceptanceRate = 0.0;
    @Builder.Default
    private Integer ranking = 0;
    @Builder.Default
    private Integer reputation = 0;
    @Builder.Default
    private Integer streakDays = 0; // ketma-ket kun
    
    // Gamification fields
    @Builder.Default
    private Integer coins = 0;        // Coin balance
    @Builder.Default
    private Integer experience = 0;   // Total XP
    @Builder.Default
    private Integer level = 1;        // Current level (starts from 1)
    @Builder.Default
    private Integer currentLevelXp = 0; // XP in current level (0-99)
    
    // Streak tracking
    private LocalDate lastLoginDate;     // Oxirgi kirgan sana
    @Builder.Default
    private Integer currentStreak = 0;   // Joriy streak (kun)
    @Builder.Default
    private Integer longestStreak = 0;   // Eng uzun streak
    @Builder.Default
    private Integer weeklyStreak = 0;    // Haftalik streak
    @Builder.Default
    private Integer monthlyStreak = 0;   // Oylik streak
    private LocalDate lastWeeklyReward;  // Oxirgi haftalik mukofot sanasi
    private LocalDate lastMonthlyReward; // Oxirgi oylik mukofot sanasi
}
