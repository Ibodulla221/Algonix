package com.code.algonix.gamification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code.algonix.messages.MessageService;
import com.code.algonix.problems.Problem;
import com.code.algonix.problems.Submission;
import com.code.algonix.problems.SubmissionRepository;
import com.code.algonix.user.UserEntity;
import com.code.algonix.user.UserStatistics;
import com.code.algonix.user.UserStatisticsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardService {

    private final UserStatisticsRepository userStatisticsRepository;
    private final SubmissionRepository submissionRepository;
    private final MessageService messageService;

    // Reward constants
    private static final int BEGINNER_COINS = 1;
    private static final int BASIC_COINS = 2;
    private static final int EASY_COINS = 3;
    private static final int NORMAL_COINS = 3;
    private static final int MEDIUM_COINS = 5;
    private static final int HARD_COINS = 8;
    
    private static final int BEGINNER_XP = 8;
    private static final int BASIC_XP = 15;
    private static final int EASY_XP = 20;
    private static final int NORMAL_XP = 25;
    private static final int MEDIUM_XP = 45;
    private static final int HARD_XP = 80;
    
    private static final int XP_PER_LEVEL = 100;

    @Transactional
    public RewardResult processSuccessfulSubmission(UserEntity user, Problem problem, Submission submission) {
        UserStatistics stats = user.getStatistics();
        if (stats == null) {
            stats = UserStatistics.builder()
                    .user(user)
                    .coins(0)
                    .experience(0)
                    .level(1)
                    .currentLevelXp(0)
                    .build();
            user.setStatistics(stats);
        }

        // Check if user already solved this problem
        boolean isFirstSolve = !hasUserSolvedProblem(user, problem, submission);
        
        log.info("Processing submission for user: {}, problem: {}, isFirstSolve: {}", 
                user.getUsername(), problem.getId(), isFirstSolve);
        
        if (!isFirstSolve) {
            // No rewards for re-solving
            return RewardResult.builder()
                    .coinsEarned(0)
                    .xpEarned(0)
                    .leveledUp(false)
                    .newLevel(stats.getLevel())
                    .message("Problem already solved - no additional rewards")
                    .build();
        }

        // Calculate rewards based on difficulty
        int coinsEarned = calculateCoins(problem.getDifficulty());
        int xpEarned = calculateXP(problem.getDifficulty());

        // Add bonus for performance (optional)
        double performanceMultiplier = calculatePerformanceMultiplier(submission);
        coinsEarned = (int) (coinsEarned * performanceMultiplier);
        xpEarned = (int) (xpEarned * performanceMultiplier);

        // Update coins
        stats.setCoins(stats.getCoins() + coinsEarned);

        // Update XP and check for level up
        int oldLevel = stats.getLevel();
        int oldXp = stats.getExperience();
        int newTotalXp = oldXp + xpEarned;
        
        stats.setExperience(newTotalXp);
        
        // Calculate new level
        int newLevel = (newTotalXp / XP_PER_LEVEL) + 1;
        int currentLevelXp = newTotalXp % XP_PER_LEVEL;
        
        stats.setLevel(newLevel);
        stats.setCurrentLevelXp(currentLevelXp);

        boolean leveledUp = newLevel > oldLevel;

        // Save statistics
        userStatisticsRepository.save(stats);

        // Message yaratish
        messageService.createProblemSolvedMessage(user, problem, coinsEarned, xpEarned, newLevel, leveledUp);

        log.info("User {} earned {} coins and {} XP for solving {} problem. Level: {} -> {}", 
                user.getUsername(), coinsEarned, xpEarned, problem.getDifficulty(), oldLevel, newLevel);

        return RewardResult.builder()
                .coinsEarned(coinsEarned)
                .xpEarned(xpEarned)
                .leveledUp(leveledUp)
                .oldLevel(oldLevel)
                .newLevel(newLevel)
                .totalCoins(stats.getCoins())
                .totalXp(stats.getExperience())
                .currentLevelXp(currentLevelXp)
                .xpToNextLevel(XP_PER_LEVEL - currentLevelXp)
                .message(buildRewardMessage(coinsEarned, xpEarned, leveledUp, newLevel))
                .build();
    }

    private int calculateCoins(Problem.Difficulty difficulty) {
        return switch (difficulty) {
            case BEGINNER -> BEGINNER_COINS;
            case BASIC -> BASIC_COINS;
            case EASY -> EASY_COINS;
            case NORMAL -> NORMAL_COINS;
            case MEDIUM -> MEDIUM_COINS;
            case HARD -> HARD_COINS;
        };
    }

    private int calculateXP(Problem.Difficulty difficulty) {
        return switch (difficulty) {
            case BEGINNER -> BEGINNER_XP;
            case BASIC -> BASIC_XP;
            case EASY -> EASY_XP;
            case NORMAL -> NORMAL_XP;
            case MEDIUM -> MEDIUM_XP;
            case HARD -> HARD_XP;
        };
    }

    private double calculatePerformanceMultiplier(Submission submission) {
        // Base multiplier
        double multiplier = 1.0;

        // Bonus for fast execution (if runtime percentile is available)
        if (submission.getRuntimePercentile() != null) {
            if (submission.getRuntimePercentile() >= 90) {
                multiplier += 0.2; // 20% bonus for top 10% performance
            } else if (submission.getRuntimePercentile() >= 75) {
                multiplier += 0.1; // 10% bonus for top 25% performance
            }
        }

        // Bonus for memory efficiency
        if (submission.getMemoryPercentile() != null) {
            if (submission.getMemoryPercentile() >= 90) {
                multiplier += 0.1; // 10% bonus for top 10% memory efficiency
            }
        }

        return Math.min(multiplier, 1.5); // Cap at 50% bonus
    }

    private boolean hasUserSolvedProblem(UserEntity user, Problem problem, Submission currentSubmission) {
        // Check if user has any OTHER ACCEPTED submission for this problem (excluding current one)
        boolean hasSolved = submissionRepository.existsByUserAndProblemAndStatusAndIdNot(
            user, problem, Submission.SubmissionStatus.ACCEPTED, currentSubmission.getId()
        );
        log.info("Checking if user {} has solved problem {} (excluding current submission {}): {}", 
                user.getUsername(), problem.getId(), currentSubmission.getId(), hasSolved);
        return hasSolved;
    }

    private String buildRewardMessage(int coins, int xp, boolean leveledUp, int newLevel) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("🎉 Tabriklaymiz! +%d coin, +%d XP", coins, xp));
        
        if (leveledUp) {
            message.append(String.format(" 🆙 Level %d ga ko'tarildingiz!", newLevel));
        }
        
        return message.toString();
    }

    public UserGameStats getUserGameStats(UserEntity user) {
        UserStatistics stats = user.getStatistics();
        if (stats == null) {
            return UserGameStats.builder()
                    .coins(0)
                    .experience(0)
                    .level(1)
                    .currentLevelXp(0)
                    .xpToNextLevel(XP_PER_LEVEL)
                    .build();
        }

        return UserGameStats.builder()
                .coins(stats.getCoins())
                .experience(stats.getExperience())
                .level(stats.getLevel())
                .currentLevelXp(stats.getCurrentLevelXp())
                .xpToNextLevel(XP_PER_LEVEL - stats.getCurrentLevelXp())
                .build();
    }

    // Test method for manual reward testing
    @Transactional
    public RewardResult testReward(UserEntity user, Problem.Difficulty difficulty) {
        log.info("Testing reward for user: {}, difficulty: {}", user.getUsername(), difficulty);
        
        UserStatistics stats = user.getStatistics();
        if (stats == null) {
            stats = UserStatistics.builder()
                    .user(user)
                    .coins(0)
                    .experience(0)
                    .level(1)
                    .currentLevelXp(0)
                    .build();
            user.setStatistics(stats);
        }

        // Calculate rewards based on difficulty
        int coinsEarned = calculateCoins(difficulty);
        int xpEarned = calculateXP(difficulty);

        // Update coins
        stats.setCoins(stats.getCoins() + coinsEarned);

        // Update XP and check for level up
        int oldLevel = stats.getLevel();
        int oldXp = stats.getExperience();
        int newTotalXp = oldXp + xpEarned;
        
        stats.setExperience(newTotalXp);
        
        // Calculate new level
        int newLevel = (newTotalXp / XP_PER_LEVEL) + 1;
        int currentLevelXp = newTotalXp % XP_PER_LEVEL;
        
        stats.setLevel(newLevel);
        stats.setCurrentLevelXp(currentLevelXp);

        boolean leveledUp = newLevel > oldLevel;

        // Save statistics
        userStatisticsRepository.save(stats);

        log.info("Test reward completed: {} earned {} coins and {} XP. Level: {} -> {}", 
                user.getUsername(), coinsEarned, xpEarned, oldLevel, newLevel);

        return RewardResult.builder()
                .coinsEarned(coinsEarned)
                .xpEarned(xpEarned)
                .leveledUp(leveledUp)
                .oldLevel(oldLevel)
                .newLevel(newLevel)
                .totalCoins(stats.getCoins())
                .totalXp(stats.getExperience())
                .currentLevelXp(currentLevelXp)
                .xpToNextLevel(XP_PER_LEVEL - currentLevelXp)
                .message(buildRewardMessage(coinsEarned, xpEarned, leveledUp, newLevel))
                .build();
    }
}