package com.code.algonix.user;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.code.algonix.user.dto.LeaderboardResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://10.36.241.244:4200", "http://172.29.0.1:4200"})
@Tag(name = "Leaderboard", description = "Foydalanuvchilar reytingi API'lari")
public class LeaderboardController {

    private final UserStatisticsRepository userStatisticsRepository;

    @GetMapping
    @Operation(summary = "Foydalanuvchilar reytingi ro'yxati")
    public ResponseEntity<LeaderboardResponse> getLeaderboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "totalSolved") String sortBy,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserStatistics> userStatsPage;

        // Search yoki sort bo'yicha ma'lumotlarni olish
        if (search != null && !search.trim().isEmpty()) {
            userStatsPage = userStatisticsRepository.findUsersByUsernameContaining(search.trim(), pageable);
        } else {
            switch (sortBy.toLowerCase()) {
                case "experience":
                    userStatsPage = userStatisticsRepository.findTopUsersByExperience(pageable);
                    break;
                case "level":
                    userStatsPage = userStatisticsRepository.findTopUsersByLevel(pageable);
                    break;
                case "currentstreak":
                    userStatsPage = userStatisticsRepository.findTopUsersByCurrentStreak(pageable);
                    break;
                case "longeststreak":
                    userStatsPage = userStatisticsRepository.findTopUsersByLongestStreak(pageable);
                    break;
                case "totalSolved":
                default:
                    userStatsPage = userStatisticsRepository.findTopUsersByTotalSolvedWithPagination(pageable);
                    break;
            }
        }

        // DTO'ga o'tkazish
        List<LeaderboardResponse.UserRankingDto> userRankings = IntStream.range(0, userStatsPage.getContent().size())
                .mapToObj(index -> {
                    UserStatistics stats = userStatsPage.getContent().get(index);
                    UserEntity user = stats.getUser();
                    
                    // Ranking hisoblash (sahifa bo'yicha)
                    int ranking = page * size + index + 1;
                    
                    // Status hisoblash (oxirgi login sanasi bo'yicha)
                    String status = calculateUserStatus(stats.getLastLoginDate());
                    
                    // Badge hisoblash
                    String badge = calculateUserBadge(stats);

                    return LeaderboardResponse.UserRankingDto.builder()
                            .userId(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .location("O'zbekiston") // Default location, keyinchalik user profile'dan olish mumkin
                            .ranking(ranking)
                            .totalSolved(stats.getTotalSolved())
                            .beginnerSolved(stats.getBeginnerSolved())
                            .basicSolved(stats.getBasicSolved())
                            .normalSolved(stats.getNormalSolved())
                            .mediumSolved(stats.getMediumSolved())
                            .hardSolved(stats.getHardSolved())
                            .acceptanceRate(stats.getAcceptanceRate())
                            .experience(stats.getExperience())
                            .level(stats.getLevel())
                            .currentLevelXp(stats.getCurrentLevelXp())
                            .coins(stats.getCoins())
                            .currentStreak(stats.getCurrentStreak())
                            .longestStreak(stats.getLongestStreak())
                            .lastLoginDate(stats.getLastLoginDate())
                            .status(status)
                            .badge(badge)
                            .build();
                })
                .toList();

        LeaderboardResponse response = LeaderboardResponse.builder()
                .users(userRankings)
                .total(userStatsPage.getTotalElements())
                .page(page)
                .pageSize(size)
                .sortBy(sortBy)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top/{count}")
    @Operation(summary = "Top N foydalanuvchilar")
    public ResponseEntity<LeaderboardResponse> getTopUsers(
            @PathVariable int count,
            @RequestParam(defaultValue = "totalSolved") String sortBy) {

        if (count > 100) count = 100; // Maksimal 100 ta user

        Pageable pageable = PageRequest.of(0, count);
        Page<UserStatistics> userStatsPage;

        switch (sortBy.toLowerCase()) {
            case "experience":
                userStatsPage = userStatisticsRepository.findTopUsersByExperience(pageable);
                break;
            case "level":
                userStatsPage = userStatisticsRepository.findTopUsersByLevel(pageable);
                break;
            case "currentstreak":
                userStatsPage = userStatisticsRepository.findTopUsersByCurrentStreak(pageable);
                break;
            case "longeststreak":
                userStatsPage = userStatisticsRepository.findTopUsersByLongestStreak(pageable);
                break;
            case "totalSolved":
            default:
                userStatsPage = userStatisticsRepository.findTopUsersByTotalSolvedWithPagination(pageable);
                break;
        }

        List<LeaderboardResponse.UserRankingDto> userRankings = IntStream.range(0, userStatsPage.getContent().size())
                .mapToObj(index -> {
                    UserStatistics stats = userStatsPage.getContent().get(index);
                    UserEntity user = stats.getUser();
                    
                    return LeaderboardResponse.UserRankingDto.builder()
                            .userId(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .location("O'zbekiston")
                            .ranking(index + 1)
                            .totalSolved(stats.getTotalSolved())
                            .beginnerSolved(stats.getBeginnerSolved())
                            .basicSolved(stats.getBasicSolved())
                            .normalSolved(stats.getNormalSolved())
                            .mediumSolved(stats.getMediumSolved())
                            .hardSolved(stats.getHardSolved())
                            .acceptanceRate(stats.getAcceptanceRate())
                            .experience(stats.getExperience())
                            .level(stats.getLevel())
                            .currentLevelXp(stats.getCurrentLevelXp())
                            .coins(stats.getCoins())
                            .currentStreak(stats.getCurrentStreak())
                            .longestStreak(stats.getLongestStreak())
                            .lastLoginDate(stats.getLastLoginDate())
                            .status(calculateUserStatus(stats.getLastLoginDate()))
                            .badge(calculateUserBadge(stats))
                            .build();
                })
                .toList();

        LeaderboardResponse response = LeaderboardResponse.builder()
                .users(userRankings)
                .total((long) userRankings.size())
                .page(0)
                .pageSize(count)
                .sortBy(sortBy)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/ranking")
    @Operation(summary = "Foydalanuvchining reytingini olish")
    public ResponseEntity<LeaderboardResponse.UserRankingDto> getUserRanking(@PathVariable Long userId) {
        UserStatistics stats = userStatisticsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User statistics not found"));

        UserEntity user = stats.getUser();
        
        // Global ranking hisoblash
        Long ranking = userStatisticsRepository.getUserRankingByTotalSolved(
                stats.getTotalSolved(), stats.getAcceptanceRate());

        LeaderboardResponse.UserRankingDto userRanking = LeaderboardResponse.UserRankingDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .location("O'zbekiston")
                .ranking(ranking.intValue())
                .totalSolved(stats.getTotalSolved())
                .beginnerSolved(stats.getBeginnerSolved())
                .basicSolved(stats.getBasicSolved())
                .normalSolved(stats.getNormalSolved())
                .mediumSolved(stats.getMediumSolved())
                .hardSolved(stats.getHardSolved())
                .acceptanceRate(stats.getAcceptanceRate())
                .experience(stats.getExperience())
                .level(stats.getLevel())
                .currentLevelXp(stats.getCurrentLevelXp())
                .coins(stats.getCoins())
                .currentStreak(stats.getCurrentStreak())
                .longestStreak(stats.getLongestStreak())
                .lastLoginDate(stats.getLastLoginDate())
                .status(calculateUserStatus(stats.getLastLoginDate()))
                .badge(calculateUserBadge(stats))
                .build();

        return ResponseEntity.ok(userRanking);
    }

    // Helper metodlar
    private String calculateUserStatus(LocalDate lastLoginDate) {
        if (lastLoginDate == null) {
            return "offline";
        }
        
        long daysSinceLogin = ChronoUnit.DAYS.between(lastLoginDate, LocalDate.now());
        
        if (daysSinceLogin == 0) {
            return "online";
        } else if (daysSinceLogin <= 3) {
            return "away";
        } else {
            return "offline";
        }
    }

    private String calculateUserBadge(UserStatistics stats) {
        int totalSolved = stats.getTotalSolved();
        int level = stats.getLevel();
        
        if (level >= 10 || totalSolved >= 100) {
            return "Master";
        } else if (level >= 7 || totalSolved >= 50) {
            return "Expert";
        } else if (level >= 5 || totalSolved >= 25) {
            return "Advanced";
        } else if (level >= 3 || totalSolved >= 10) {
            return "Intermediate";
        } else {
            return "Beginner";
        }
    }
}