package com.code.algonix.user;

import com.code.algonix.gamification.RewardService;
import com.code.algonix.gamification.UserGameStats;
import com.code.algonix.messages.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    
    private final RewardService rewardService;
    private final MessageService messageService;
    
    /**
     * Foydalanuvchi profil ma'lumotlari
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(@AuthenticationPrincipal UserEntity user) {
        UserGameStats gameStats = rewardService.getUserGameStats(user);
        long unreadMessages = messageService.getUnreadCount(user);
        
        UserStatistics stats = user.getStatistics();
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("role", user.getRole());
        
        // Game statistics
        profile.put("coins", gameStats.getCoins());
        profile.put("experience", gameStats.getExperience());
        profile.put("level", gameStats.getLevel());
        profile.put("currentLevelXp", gameStats.getCurrentLevelXp());
        profile.put("xpToNextLevel", gameStats.getXpToNextLevel());
        
        // Problem solving statistics
        if (stats != null) {
            profile.put("totalSolved", stats.getTotalSolved());
            profile.put("beginnerSolved", stats.getBeginnerSolved());
            profile.put("basicSolved", stats.getBasicSolved());
            profile.put("normalSolved", stats.getNormalSolved());
            profile.put("mediumSolved", stats.getMediumSolved());
            profile.put("hardSolved", stats.getHardSolved());
            profile.put("acceptanceRate", stats.getAcceptanceRate());
            profile.put("ranking", stats.getRanking());
            profile.put("reputation", stats.getReputation());
            
            // Streak information
            profile.put("currentStreak", stats.getCurrentStreak());
            profile.put("longestStreak", stats.getLongestStreak());
            profile.put("weeklyStreak", stats.getWeeklyStreak());
            profile.put("monthlyStreak", stats.getMonthlyStreak());
            profile.put("lastLoginDate", stats.getLastLoginDate());
        } else {
            // Default values if no statistics
            profile.put("totalSolved", 0);
            profile.put("beginnerSolved", 0);
            profile.put("basicSolved", 0);
            profile.put("normalSolved", 0);
            profile.put("mediumSolved", 0);
            profile.put("hardSolved", 0);
            profile.put("acceptanceRate", 0.0);
            profile.put("ranking", 0);
            profile.put("reputation", 0);
            profile.put("currentStreak", 0);
            profile.put("longestStreak", 0);
            profile.put("weeklyStreak", 0);
            profile.put("monthlyStreak", 0);
            profile.put("lastLoginDate", null);
        }
        
        // Messages
        profile.put("unreadMessages", unreadMessages);
        
        return ResponseEntity.ok(profile);
    }
    
    /**
     * Foydalanuvchi dashboard ma'lumotlari
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@AuthenticationPrincipal UserEntity user) {
        UserGameStats gameStats = rewardService.getUserGameStats(user);
        long unreadMessages = messageService.getUnreadCount(user);
        
        UserStatistics stats = user.getStatistics();
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // Quick stats
        dashboard.put("coins", gameStats.getCoins());
        dashboard.put("level", gameStats.getLevel());
        dashboard.put("currentLevelXp", gameStats.getCurrentLevelXp());
        dashboard.put("xpToNextLevel", gameStats.getXpToNextLevel());
        dashboard.put("unreadMessages", unreadMessages);
        
        if (stats != null) {
            dashboard.put("totalSolved", stats.getTotalSolved());
            dashboard.put("currentStreak", stats.getCurrentStreak());
            dashboard.put("longestStreak", stats.getLongestStreak());
        } else {
            dashboard.put("totalSolved", 0);
            dashboard.put("currentStreak", 0);
            dashboard.put("longestStreak", 0);
        }
        
        return ResponseEntity.ok(dashboard);
    }
}