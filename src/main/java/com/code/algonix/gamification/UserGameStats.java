package com.code.algonix.gamification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGameStats {
    private int coins;
    private int experience;
    private int level;
    private int currentLevelXp;  // XP in current level (0-99)
    private int xpToNextLevel;   // XP needed for next level
}