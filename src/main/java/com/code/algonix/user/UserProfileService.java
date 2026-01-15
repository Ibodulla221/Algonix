package com.code.algonix.user;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.code.algonix.exception.ResourceNotFoundException;
import com.code.algonix.problems.Problem;
import com.code.algonix.problems.ProblemRepository;
import com.code.algonix.problems.SubmissionRepository;
import com.code.algonix.user.dto.CategoryStatsResponse;
import com.code.algonix.user.dto.ChangePasswordRequest;
import com.code.algonix.user.dto.DifficultyStatsResponse;
import com.code.algonix.user.dto.UpdateProfileRequest;
import com.code.algonix.user.dto.UserProfileResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;
    
    // Avatar upload directory
    private static final String AVATAR_UPLOAD_DIR = "uploads/avatars/";
    private static final String AVATAR_BASE_URL = "/api/files/avatars/";

    public UserProfileResponse getUserProfile(String username, String currentUsername) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if profile is public or if it's the user's own profile
        boolean isOwnProfile = username.equals(currentUsername);
        if (!isOwnProfile && !user.getIsProfilePublic()) {
            throw new RuntimeException("Profile is private");
        }

        return buildUserProfileResponse(user, isOwnProfile);
    }

    public UserProfileResponse getCurrentUserProfile(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return buildUserProfileResponse(user, true);
    }

    @Transactional
    public UserProfileResponse updateProfile(String username, UpdateProfileRequest request) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update profile fields
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName().trim());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail().trim());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio().trim());
        }
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation().trim());
        }
        if (request.getCompany() != null) {
            user.setCompany(request.getCompany().trim());
        }
        if (request.getJobTitle() != null) {
            user.setJobTitle(request.getJobTitle().trim());
        }
        if (request.getWebsite() != null) {
            user.setWebsite(request.getWebsite().trim());
        }
        if (request.getGithubUsername() != null) {
            user.setGithubUsername(request.getGithubUsername().trim());
        }
        if (request.getLinkedinUrl() != null) {
            user.setLinkedinUrl(request.getLinkedinUrl().trim());
        }
        if (request.getTwitterUsername() != null) {
            user.setTwitterUsername(request.getTwitterUsername().trim());
        }

        // Update privacy settings
        if (request.getIsProfilePublic() != null) {
            user.setIsProfilePublic(request.getIsProfilePublic());
        }
        if (request.getShowEmail() != null) {
            user.setShowEmail(request.getShowEmail());
        }
        if (request.getShowLocation() != null) {
            user.setShowLocation(request.getShowLocation());
        }
        if (request.getShowCompany() != null) {
            user.setShowCompany(request.getShowCompany());
        }

        userRepository.save(user);
        return buildUserProfileResponse(user, true);
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Verify new password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New password and confirmation do not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public UserProfileResponse uploadAvatar(String username, MultipartFile file) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        try {
            // Validate file
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("File must be an image");
            }

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(AVATAR_UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String fileName = username + "_" + UUID.randomUUID().toString() + fileExtension;

            // Save file
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Delete old avatar if exists
            if (user.getAvatarFileName() != null) {
                try {
                    Path oldFilePath = uploadPath.resolve(user.getAvatarFileName());
                    Files.deleteIfExists(oldFilePath);
                } catch (IOException e) {
                    // Log error but don't fail the upload
                    System.err.println("Failed to delete old avatar: " + e.getMessage());
                }
            }

            // Update user
            user.setAvatarFileName(fileName);
            user.setAvatarUrl(AVATAR_BASE_URL + fileName);
            userRepository.save(user);

            return buildUserProfileResponse(user, true);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload avatar: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteAvatar(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getAvatarFileName() != null) {
            try {
                Path filePath = Paths.get(AVATAR_UPLOAD_DIR).resolve(user.getAvatarFileName());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log error but don't fail the deletion
                System.err.println("Failed to delete avatar file: " + e.getMessage());
            }

            user.setAvatarFileName(null);
            user.setAvatarUrl(null);
            userRepository.save(user);
        }
    }

    public DifficultyStatsResponse getDifficultyStats(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get total problems count
        Long totalProblems = problemRepository.count();
        
        // Get user's total solved problems
        Long userSolvedProblems = submissionRepository.countSolvedProblemsByUser(user);

        // Get difficulty statistics
        List<DifficultyStatsResponse.DifficultyStatDto> difficultyStats = new ArrayList<>();
        
        for (Problem.Difficulty difficulty : Problem.Difficulty.values()) {
            Long totalByDifficulty = problemRepository.countByDifficulty(difficulty);
            Long solvedByDifficulty = submissionRepository.countSolvedProblemsByUserAndDifficulty(user, difficulty);
            
            difficultyStats.add(DifficultyStatsResponse.DifficultyStatDto.builder()
                    .name(difficulty.name())
                    .total(totalByDifficulty.intValue())
                    .solved(solvedByDifficulty.intValue())
                    .build());
        }

        return DifficultyStatsResponse.builder()
                .allProblems(totalProblems.intValue())
                .allUserSolvedProblems(userSolvedProblems.intValue())
                .difficultyStats(difficultyStats)
                .build();
    }

    public CategoryStatsResponse getCategoryStats(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get total problems count
        Long totalProblems = problemRepository.count();
        
        // Get user's total solved problems
        Long userSolvedProblems = submissionRepository.countSolvedProblemsByUser(user);

        // Get all categories with their problem counts
        List<Object[]> categoryData = problemRepository.countByCategory();
        
        // Get user's solved problems by category
        List<CategoryStatsResponse.CategoryStatDto> categoryStats = new ArrayList<>();
        
        for (Object[] data : categoryData) {
            String categoryName = (String) data[0];
            Long totalByCategory = (Long) data[1];
            
            // Count user's solved problems in this category
            Long solvedByCategory = submissionRepository.countSolvedProblemsByUserAndCategory(user, categoryName);
            
            categoryStats.add(CategoryStatsResponse.CategoryStatDto.builder()
                    .name(categoryName)
                    .total(totalByCategory.intValue())
                    .solved(solvedByCategory.intValue())
                    .build());
        }

        return CategoryStatsResponse.builder()
                .allProblems(totalProblems.intValue())
                .allUserSolvedProblems(userSolvedProblems.intValue())
                .categoryStats(categoryStats)
                .build();
    }

    public Map<String, Object> getDailyProblemStats(String username, Integer year, Integer month) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Map<String, Object> dailyStats = new HashMap<>();
        
        // Validation
        if (month < 1 || month > 12) {
            throw new RuntimeException("Month must be between 1 and 12");
        }
        
        // Oyning kunlari sonini aniqlash
        int daysInMonth = LocalDateTime.of(year, month, 1, 0, 0).toLocalDate().lengthOfMonth();
        
        // Kunlik ma'lumotlarni tayyorlash
        Map<Integer, Long> dailyMap = new HashMap<>();
        for (int i = 1; i <= daysInMonth; i++) {
            dailyMap.put(i, 0L);
        }
        
        // Foydalanuvchining shu oydagi yechgan masalalarini olish
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1);
        
        // Kunlik yechilgan masalalar sonini hisoblash
        List<Object[]> dailyData = submissionRepository.findDailySolvedProblemsByUserAndMonth(user, startDate, endDate);
        
        for (Object[] stat : dailyData) {
            Integer day = ((Number) stat[0]).intValue();
            Long count = ((Number) stat[1]).longValue();
            if (day >= 1 && day <= daysInMonth) {
                dailyMap.put(day, count);
            }
        }
        
        // Chart uchun format
        String[] labels = new String[daysInMonth];
        int[] values = new int[daysInMonth];
        
        for (int i = 1; i <= daysInMonth; i++) {
            labels[i - 1] = String.valueOf(i);
            values[i - 1] = dailyMap.get(i).intValue();
        }
        
        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                              "July", "August", "September", "October", "November", "December"};
        
        dailyStats.put("labels", labels);
        dailyStats.put("values", values);
        dailyStats.put("year", year);
        dailyStats.put("month", month);
        dailyStats.put("monthName", monthNames[month - 1]);
        dailyStats.put("title", "Daily Problems Solved in " + monthNames[month - 1] + " " + year);
        dailyStats.put("totalProblems", dailyMap.values().stream().mapToLong(Long::longValue).sum());
        
        return dailyStats;
    }

    private UserProfileResponse buildUserProfileResponse(UserEntity user, boolean isOwnProfile) {
        UserStatistics stats = user.getStatistics();

        UserProfileResponse.UserStatisticsDto statisticsDto = null;
        if (stats != null) {
            statisticsDto = UserProfileResponse.UserStatisticsDto.builder()
                    .totalSolved(stats.getTotalSolved())
                    .beginnerSolved(stats.getBeginnerSolved())
                    .basicSolved(stats.getBasicSolved())
                    .normalSolved(stats.getNormalSolved())
                    .mediumSolved(stats.getMediumSolved())
                    .hardSolved(stats.getHardSolved())
                    .acceptanceRate(stats.getAcceptanceRate())
                    .ranking(stats.getRanking())
                    .reputation(stats.getReputation())
                    .streakDays(stats.getStreakDays())
                    .coins(stats.getCoins())
                    .experience(stats.getExperience())
                    .level(stats.getLevel())
                    .currentLevelXp(stats.getCurrentLevelXp())
                    .currentStreak(stats.getCurrentStreak())
                    .longestStreak(stats.getLongestStreak())
                    .weeklyStreak(stats.getWeeklyStreak())
                    .monthlyStreak(stats.getMonthlyStreak())
                    .lastLoginDate(stats.getLastLoginDate())
                    .build();
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(isOwnProfile || user.getShowEmail() ? user.getEmail() : null)
                .role(user.getRole())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .displayName(user.getDisplayName())
                .bio(user.getBio())
                .location(isOwnProfile || user.getShowLocation() ? user.getLocation() : null)
                .company(isOwnProfile || user.getShowCompany() ? user.getCompany() : null)
                .jobTitle(user.getJobTitle())
                .website(user.getWebsite())
                .githubUsername(user.getGithubUsername())
                .linkedinUrl(user.getLinkedinUrl())
                .twitterUsername(user.getTwitterUsername())
                .avatarUrl(user.getAvatarUrl())
                .isProfilePublic(isOwnProfile ? user.getIsProfilePublic() : null)
                .showEmail(isOwnProfile ? user.getShowEmail() : null)
                .showLocation(isOwnProfile ? user.getShowLocation() : null)
                .showCompany(isOwnProfile ? user.getShowCompany() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .statistics(statisticsDto)
                .build();
    }
}