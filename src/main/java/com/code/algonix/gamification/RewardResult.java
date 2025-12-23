package com.code.algonix.gamification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardResult {
    private int coinsEarned;
    private int xpEarned;
    private boolean leveledUp;
    private int oldLevel;
    private int newLevel;
    
    // Current totals
    private int totalCoins;
    private int totalXp;
    private int currentLevelXp;
    private int xpToNextLevel;
    
    private String message;
}