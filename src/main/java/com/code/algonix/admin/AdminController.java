package com.code.algonix.admin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.code.algonix.admin.dto.AdminSubmissionResponse;
import com.code.algonix.admin.dto.AdminUserResponse;
import com.code.algonix.admin.dto.BroadcastMessageRequest;
import com.code.algonix.messages.MessageService;
import com.code.algonix.problems.Problem;
import com.code.algonix.problems.ProblemRepository;
import com.code.algonix.problems.Submission;
import com.code.algonix.problems.SubmissionRepository;
import com.code.algonix.user.Role;
import com.code.algonix.user.UserEntity;
import com.code.algonix.user.UserRepository;
import com.code.algonix.user.UserStatisticsRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;
    private final UserStatisticsRepository userStatisticsRepository;
    private final MessageService messageService;
    
    /**
     * Admin Dashboard - Umumiy statistikalar
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Foydalanuvchilar statistikasi
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByRole(Role.USER);
        long adminUsers = userRepository.countByRole(Role.ADMIN);
        
        // Masalalar statistikasi
        long totalProblems = problemRepository.count();
        long beginnerProblems = problemRepository.countByDifficulty(Problem.Difficulty.BEGINNER);
        long basicProblems = problemRepository.countByDifficulty(Problem.Difficulty.BASIC);
        long normalProblems = problemRepository.countByDifficulty(Problem.Difficulty.NORMAL);
        long mediumProblems = problemRepository.countByDifficulty(Problem.Difficulty.MEDIUM);
        long hardProblems = problemRepository.countByDifficulty(Problem.Difficulty.HARD);
        
        // Submission statistikasi
        long totalSubmissions = submissionRepository.count();
        long acceptedSubmissions = submissionRepository.countByStatus(Submission.SubmissionStatus.ACCEPTED);
        long pendingSubmissions = submissionRepository.countByStatus(Submission.SubmissionStatus.PENDING);
        
        // Bugungi faollik
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        long todaySubmissions = submissionRepository.countBySubmittedAtAfter(today);
        
        // Dashboard ma'lumotlari
        dashboard.put("users", Map.of(
                "total", totalUsers,
                "active", activeUsers,
                "admins", adminUsers
        ));
        
        dashboard.put("problems", Map.of(
                "total", totalProblems,
                "beginner", beginnerProblems,
                "basic", basicProblems,
                "normal", normalProblems,
                "medium", mediumProblems,
                "hard", hardProblems
        ));
        
        dashboard.put("submissions", Map.of(
                "total", totalSubmissions,
                "accepted", acceptedSubmissions,
                "pending", pendingSubmissions,
                "today", todaySubmissions,
                "acceptanceRate", totalSubmissions > 0 ? (double) acceptedSubmissions / totalSubmissions * 100 : 0
        ));
        
        return ResponseEntity.ok(dashboard);
    }
    
    /**
     * Barcha foydalanuvchilarni ko'rish
     */
    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserEntity> users = userRepository.findAll(pageable);
        
        Page<AdminUserResponse> userResponses = users.map(user -> {
            var stats = user.getStatistics();
            return AdminUserResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .totalSolved(stats != null ? stats.getTotalSolved() : 0)
                    .coins(stats != null ? stats.getCoins() : 0)
                    .level(stats != null ? stats.getLevel() : 1)
                    .currentStreak(stats != null ? stats.getCurrentStreak() : 0)
                    .lastLoginDate(stats != null ? stats.getLastLoginDate() : null)
                    .build();
        });
        
        return ResponseEntity.ok(userResponses);
    }
    
    /**
     * Foydalanuvchini admin qilish
     */
    @PutMapping("/users/{userId}/make-admin")
    public ResponseEntity<Map<String, String>> makeUserAdmin(@PathVariable Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setRole(Role.ADMIN);
        userRepository.save(user);
        
        // Admin qilinganlik haqida message yuborish
        messageService.createSystemMessage(user, 
                "🎉 Admin huquqlari berildi!", 
                "Tabriklaymiz! Sizga admin huquqlari berildi. Endi siz platformani boshqarishingiz mumkin.");
        
        return ResponseEntity.ok(Map.of("message", "User successfully made admin"));
    }
    
    /**
     * Foydalanuvchini block qilish
     */
    @PutMapping("/users/{userId}/block")
    public ResponseEntity<Map<String, String>> blockUser(@PathVariable Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Bu yerda user block logic qo'shish kerak
        // Hozircha message yuboramiz
        messageService.createSystemMessage(user, 
                "⚠️ Account bloklandi", 
                "Sizning accountingiz qoidalarni buzganlik uchun vaqtincha bloklandi. Agar bu xato deb hisoblasangiz, support bilan bog'laning.");
        
        return ResponseEntity.ok(Map.of("message", "User blocked successfully"));
    }
    
    /**
     * Barcha masalalarni ko'rish
     */
    @GetMapping("/problems")
    public ResponseEntity<Page<Problem>> getProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Problem.Difficulty difficulty) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        
        Page<Problem> problems;
        if (difficulty != null) {
            problems = problemRepository.findByDifficulty(difficulty, pageable);
        } else {
            problems = problemRepository.findAll(pageable);
        }
        
        return ResponseEntity.ok(problems);
    }
    
    /**
     * Masalani o'chirish
     */
    @DeleteMapping("/problems/{problemId}")
    public ResponseEntity<Map<String, String>> deleteProblem(@PathVariable Long problemId) {
        if (!problemRepository.existsById(problemId)) {
            return ResponseEntity.notFound().build();
        }
        
        problemRepository.deleteById(problemId);
        return ResponseEntity.ok(Map.of("message", "Problem deleted successfully"));
    }
    
    /**
     * Oxirgi submissionlar
     */
    @GetMapping("/submissions/recent")
    public ResponseEntity<List<AdminSubmissionResponse>> getRecentSubmissions(
            @RequestParam(defaultValue = "50") int limit) {
        
        Pageable pageable = PageRequest.of(0, limit, Sort.by("submittedAt").descending());
        Page<Submission> submissions = submissionRepository.findAll(pageable);
        
        List<AdminSubmissionResponse> responses = submissions.getContent().stream()
                .map(submission -> AdminSubmissionResponse.builder()
                        .id(submission.getId())
                        .username(submission.getUser().getUsername())
                        .problemTitle(submission.getProblem().getTitle())
                        .language(submission.getLanguage())
                        .status(submission.getStatus())
                        .submittedAt(submission.getSubmittedAt())
                        .runtime(submission.getRuntime())
                        .memory(submission.getMemory())
                        .build())
                .toList();
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Barcha foydalanuvchilarga system message yuborish
     */
    @PostMapping("/broadcast-message")
    public ResponseEntity<Map<String, String>> broadcastMessage(
            @RequestBody BroadcastMessageRequest request) {
        
        List<UserEntity> users = userRepository.findAll();
        
        for (UserEntity user : users) {
            messageService.createSystemMessage(user, request.getTitle(), request.getContent());
        }
        
        return ResponseEntity.ok(Map.of(
                "message", "Message sent to all users",
                "userCount", String.valueOf(users.size())
        ));
    }
    
    /**
     * Platform statistikalari (grafik uchun)
     */
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Oxirgi 7 kunlik submission statistikasi
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        List<Object[]> dailySubmissions = submissionRepository.findDailySubmissionStats(weekAgo);
        
        // Difficulty bo'yicha masalalar taqsimoti
        Map<String, Long> problemsByDifficulty = Map.of(
                "BEGINNER", problemRepository.countByDifficulty(Problem.Difficulty.BEGINNER),
                "BASIC", problemRepository.countByDifficulty(Problem.Difficulty.BASIC),
                "NORMAL", problemRepository.countByDifficulty(Problem.Difficulty.NORMAL),
                "MEDIUM", problemRepository.countByDifficulty(Problem.Difficulty.MEDIUM),
                "HARD", problemRepository.countByDifficulty(Problem.Difficulty.HARD)
        );
        
        // Top foydalanuvchilar
        List<Object[]> topUsers = userStatisticsRepository.findTopUsersByTotalSolved(PageRequest.of(0, 10));
        
        analytics.put("dailySubmissions", dailySubmissions);
        analytics.put("problemsByDifficulty", problemsByDifficulty);
        analytics.put("topUsers", topUsers);
        
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Masalalar yaratilish statistikasi (Admin panel uchun)
     */
    @GetMapping("/problems/statistics")
    public ResponseEntity<Map<String, Object>> getProblemStatistics(
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "monthly") String type) {
        
        Map<String, Object> statistics = new HashMap<>();
        
        if ("yearly".equals(type)) {
            // Yillik statistika
            List<Object[]> yearlyStats = problemRepository.findYearlyProblemCreationStats();
            statistics.put("yearlyStats", yearlyStats);
            statistics.put("availableYears", problemRepository.findAvailableYears());
        } else {
            // Oylik statistika
            if (year != null) {
                // Belgilangan yil uchun oylik statistika
                List<Object[]> monthlyStats = problemRepository.findMonthlyProblemCreationStatsByYear(year);
                List<Object[]> difficultyStats = problemRepository.findProblemCreationStatsByDifficultyAndYear(year);
                Long totalForYear = problemRepository.countProblemsByYear(year);
                
                // Oylik ma'lumotlarni formatlash (1-12 oy uchun)
                Map<Integer, Long> monthlyData = new HashMap<>();
                for (int i = 1; i <= 12; i++) {
                    monthlyData.put(i, 0L);
                }
                
                for (Object[] stat : monthlyStats) {
                    Integer month = ((Number) stat[0]).intValue();
                    Long count = ((Number) stat[1]).longValue();
                    monthlyData.put(month, count);
                }
                
                // Qiyinlik darajasi bo'yicha oylik ma'lumotlar
                Map<String, Map<Integer, Long>> difficultyMonthlyData = new HashMap<>();
                for (Problem.Difficulty difficulty : Problem.Difficulty.values()) {
                    Map<Integer, Long> monthData = new HashMap<>();
                    for (int i = 1; i <= 12; i++) {
                        monthData.put(i, 0L);
                    }
                    difficultyMonthlyData.put(difficulty.name(), monthData);
                }
                
                for (Object[] stat : difficultyStats) {
                    String difficulty = stat[0].toString();
                    Integer month = ((Number) stat[1]).intValue();
                    Long count = ((Number) stat[2]).longValue();
                    difficultyMonthlyData.get(difficulty).put(month, count);
                }
                
                statistics.put("year", year);
                statistics.put("monthlyStats", monthlyData);
                statistics.put("difficultyStats", difficultyMonthlyData);
                statistics.put("totalForYear", totalForYear);
            } else {
                // Umumiy oylik statistika (barcha yillar)
                List<Object[]> monthlyStats = problemRepository.findMonthlyProblemCreationStats();
                statistics.put("monthlyStats", monthlyStats);
            }
            
            statistics.put("availableYears", problemRepository.findAvailableYears());
        }
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Masalalar yaratilish grafigi uchun ma'lumotlar
     */
    @GetMapping("/problems/chart-data")
    public ResponseEntity<Map<String, Object>> getProblemChartData(
            @RequestParam(required = false) Integer year) {
        
        Map<String, Object> chartData = new HashMap<>();
        
        if (year == null) {
            year = LocalDateTime.now().getYear();
        }
        
        // Oylik ma'lumotlarni olish
        List<Object[]> monthlyStats = problemRepository.findMonthlyProblemCreationStatsByYear(year);
        
        // 12 oylik ma'lumotlarni tayyorlash
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        double[] values = new double[12];
        
        // Barcha oylarni 0 bilan boshlash
        for (int i = 0; i < 12; i++) {
            values[i] = 0.0;
        }
        
        // Database'dan olingan ma'lumotlarni joylashtirish
        for (Object[] stat : monthlyStats) {
            Integer month = ((Number) stat[0]).intValue();
            Long count = ((Number) stat[1]).longValue();
            if (month >= 1 && month <= 12) {
                values[month - 1] = count.doubleValue();
            }
        }
        
        chartData.put("labels", months);
        chartData.put("values", values);
        chartData.put("year", year);
        chartData.put("title", "Problems Created in " + year);
        chartData.put("availableYears", problemRepository.findAvailableYears());
        
        return ResponseEntity.ok(chartData);
    }
    
    /**
     * Belgilangan oy uchun kunlik masalalar statistikasi
     */
    @GetMapping("/problems/daily-stats")
    public ResponseEntity<Map<String, Object>> getDailyProblemStats(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        
        Map<String, Object> dailyStats = new HashMap<>();
        
        // Validation
        if (month < 1 || month > 12) {
            return ResponseEntity.badRequest().body(Map.of("error", "Month must be between 1 and 12"));
        }
        
        // Kunlik ma'lumotlarni olish
        List<Object[]> dailyData = problemRepository.findDailyProblemCreationStatsByYearAndMonth(year, month);
        
        // Oyning kunlari sonini aniqlash
        int daysInMonth = LocalDateTime.of(year, month, 1, 0, 0).toLocalDate().lengthOfMonth();
        
        // Kunlik ma'lumotlarni tayyorlash
        Map<Integer, Long> dailyMap = new HashMap<>();
        for (int i = 1; i <= daysInMonth; i++) {
            dailyMap.put(i, 0L);
        }
        
        // Database'dan olingan ma'lumotlarni joylashtirish
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
        dailyStats.put("title", "Daily Problems Created in " + monthNames[month - 1] + " " + year);
        dailyStats.put("totalProblems", dailyMap.values().stream().mapToLong(Long::longValue).sum());
        
        return ResponseEntity.ok(dailyStats);
    }
    
    /**
     * Yillik masalalar yaratilish statistikasi (oylik breakdown)
     */
    @GetMapping("/problems/yearly-stats")
    public ResponseEntity<Map<String, Object>> getYearlyProblemStats(
            @RequestParam(required = false) Integer year) {
        
        if (year == null) {
            year = LocalDateTime.now().getYear();
        }
        
        Map<String, Object> yearlyStats = new HashMap<>();
        
        // Oylik ma'lumotlarni olish
        List<Object[]> monthlyStats = problemRepository.findMonthlyProblemCreationStatsByYear(year);
        
        // 12 oylik ma'lumotlarni tayyorlash
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int[] values = new int[12];
        
        // Barcha oylarni 0 bilan boshlash
        for (int i = 0; i < 12; i++) {
            values[i] = 0;
        }
        
        // Database'dan olingan ma'lumotlarni joylashtirish
        for (Object[] stat : monthlyStats) {
            Integer month = ((Number) stat[0]).intValue();
            Long count = ((Number) stat[1]).longValue();
            if (month >= 1 && month <= 12) {
                values[month - 1] = count.intValue();
            }
        }
        
        yearlyStats.put("labels", months);
        yearlyStats.put("values", values);
        yearlyStats.put("year", year);
        yearlyStats.put("title", "Problems Created in " + year);
        yearlyStats.put("availableYears", problemRepository.findAvailableYears());
        
        return ResponseEntity.ok(yearlyStats);
    }
    
    /**
     * Foydalanuvchilar ro'yxatdan o'tish statistikasi
     */
    @GetMapping("/users/registration-stats")
    public ResponseEntity<Map<String, Object>> getUserRegistrationStats(
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "monthly") String type) {
        
        Map<String, Object> statistics = new HashMap<>();
        
        if ("yearly".equals(type)) {
            // Yillik statistika
            List<Object[]> yearlyStats = userRepository.findYearlyUserRegistrationStats();
            statistics.put("yearlyStats", yearlyStats);
            statistics.put("availableYears", userRepository.findAvailableRegistrationYears());
        } else {
            // Oylik statistika
            if (year != null) {
                // Belgilangan yil uchun oylik statistika
                List<Object[]> monthlyStats = userRepository.findMonthlyUserRegistrationStatsByYear(year);
                List<Object[]> roleStats = userRepository.findUserRegistrationStatsByRoleAndYear(year);
                Long totalForYear = userRepository.countUsersByYear(year);
                
                // Oylik ma'lumotlarni formatlash (1-12 oy uchun)
                Map<Integer, Long> monthlyData = new HashMap<>();
                for (int i = 1; i <= 12; i++) {
                    monthlyData.put(i, 0L);
                }
                
                for (Object[] stat : monthlyStats) {
                    Integer month = ((Number) stat[0]).intValue();
                    Long count = ((Number) stat[1]).longValue();
                    monthlyData.put(month, count);
                }
                
                // Role bo'yicha oylik ma'lumotlar
                Map<String, Map<Integer, Long>> roleMonthlyData = new HashMap<>();
                for (Role role : Role.values()) {
                    Map<Integer, Long> monthData = new HashMap<>();
                    for (int i = 1; i <= 12; i++) {
                        monthData.put(i, 0L);
                    }
                    roleMonthlyData.put(role.name(), monthData);
                }
                
                for (Object[] stat : roleStats) {
                    String role = stat[0].toString();
                    Integer month = ((Number) stat[1]).intValue();
                    Long count = ((Number) stat[2]).longValue();
                    roleMonthlyData.get(role).put(month, count);
                }
                
                statistics.put("year", year);
                statistics.put("monthlyStats", monthlyData);
                statistics.put("roleStats", roleMonthlyData);
                statistics.put("totalForYear", totalForYear);
            } else {
                // Umumiy oylik statistika (barcha yillar)
                List<Object[]> monthlyStats = userRepository.findYearlyUserRegistrationStats();
                statistics.put("monthlyStats", monthlyStats);
            }
            
            statistics.put("availableYears", userRepository.findAvailableRegistrationYears());
        }
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Foydalanuvchilar ro'yxatdan o'tish grafigi uchun ma'lumotlar
     */
    @GetMapping("/users/registration-chart-data")
    public ResponseEntity<Map<String, Object>> getUserRegistrationChartData(
            @RequestParam(required = false) Integer year) {
        
        Map<String, Object> chartData = new HashMap<>();
        
        if (year == null) {
            year = LocalDateTime.now().getYear();
        }
        
        // Faqat USER role'dagi foydalanuvchilar uchun statistika
        List<Object[]> roleStats = userRepository.findUserRegistrationStatsByRoleAndYear(year);
        
        // USER role'dagi foydalanuvchilar sonini hisoblash
        Long totalUsersForYear = 0L;
        for (Object[] stat : roleStats) {
            String role = stat[0].toString();
            if ("USER".equals(role)) {
                Long count = ((Number) stat[2]).longValue();
                totalUsersForYear += count;
            }
        }
        
        // 12 oylik ma'lumotlarni tayyorlash
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        
        // Monthly stats array (0-11 for 12 months) - faqat USER'lar uchun
        int[] userValues = new int[12];
        
        // Initialize with zeros
        for (int i = 0; i < 12; i++) {
            userValues[i] = 0;
        }
        
        // Fill USER role stats only
        for (Object[] stat : roleStats) {
            String role = stat[0].toString();
            Integer month = ((Number) stat[1]).intValue();
            Long count = ((Number) stat[2]).longValue();
            
            if (month >= 1 && month <= 12 && "USER".equals(role)) {
                userValues[month - 1] = count.intValue();
            }
        }
        
        // Build response in requested format (faqat USER'lar uchun)
        chartData.put("year", year);
        chartData.put("availableYears", userRepository.findAvailableRegistrationYears());
        chartData.put("USER", userValues);
        chartData.put("monthlyStats", userValues); // monthlyStats = USER values
        chartData.put("labels", months);
        chartData.put("totalForYear", totalUsersForYear.intValue());
        
        return ResponseEntity.ok(chartData);
    }
    
    /**
     * Oy oralig'ini olish (MM.YYYY formatida)
     */
    @GetMapping("/months-range")
    public ResponseEntity<List<String>> getMonthsRange(
            @RequestParam String startDate,
            @RequestParam(defaultValue = "12") int count) {
        
        try {
            // startDate formatini parse qilish (YYYY-MM yoki MM.YYYY)
            String[] parts;
            int year, month;
            
            if (startDate.contains("-")) {
                // YYYY-MM format
                parts = startDate.split("-");
                year = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]);
            } else if (startDate.contains(".")) {
                // MM.YYYY format
                parts = startDate.split("\\.");
                month = Integer.parseInt(parts[0]);
                year = Integer.parseInt(parts[1]);
            } else {
                return ResponseEntity.badRequest().build();
            }
            
            // Validation
            if (month < 1 || month > 12 || year < 1900 || year > 2100) {
                return ResponseEntity.badRequest().build();
            }
            
            List<String> monthsRange = new ArrayList<>();
            
            // Ketma-ket oylarni generate qilish
            for (int i = 0; i < count; i++) {
                // MM.YYYY formatida qo'shish
                String monthStr = String.format("%02d.%d", month, year);
                monthsRange.add(monthStr);
                
                // Keyingi oyga o'tish
                month++;
                if (month > 12) {
                    month = 1;
                    year++;
                }
            }
            
            return ResponseEntity.ok(monthsRange);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Yillik yechilgan masalalar statistikasi (oylik breakdown)
     */
    @GetMapping("/submissions/yearly-solved-stats")
    public ResponseEntity<Map<String, Object>> getYearlySolvedProblemsStats(
            @RequestParam(required = false) Integer year) {
        
        if (year == null) {
            year = LocalDateTime.now().getYear();
        }
        
        Map<String, Object> yearlyStats = new HashMap<>();
        
        // Oylik yechilgan masalalar statistikasi
        List<Object[]> monthlyStats = submissionRepository.findMonthlySolvedProblemsByYear(year);
        
        // 12 oylik ma'lumotlarni tayyorlash
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int[] values = new int[12];
        
        // Barcha oylarni 0 bilan boshlash
        for (int i = 0; i < 12; i++) {
            values[i] = 0;
        }
        
        // Database'dan olingan ma'lumotlarni joylashtirish
        for (Object[] stat : monthlyStats) {
            Integer month = ((Number) stat[0]).intValue();
            Long count = ((Number) stat[1]).longValue();
            if (month >= 1 && month <= 12) {
                values[month - 1] = count.intValue();
            }
        }
        
        yearlyStats.put("labels", months);
        yearlyStats.put("values", values);
        yearlyStats.put("year", year);
        yearlyStats.put("title", "Problems Solved in " + year);
        yearlyStats.put("availableYears", submissionRepository.findAvailableSubmissionYears());
        
        return ResponseEntity.ok(yearlyStats);
    }
    
    /**
     * Kunlik yechilgan masalalar statistikasi
     */
    @GetMapping("/submissions/daily-solved-stats")
    public ResponseEntity<Map<String, Object>> getDailySolvedProblemsStats(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        
        Map<String, Object> dailyStats = new HashMap<>();
        
        // Validation
        if (month < 1 || month > 12) {
            return ResponseEntity.badRequest().body(Map.of("error", "Month must be between 1 and 12"));
        }
        
        // Kunlik yechilgan masalalar statistikasi
        List<Object[]> dailyData = submissionRepository.findDailySolvedProblemsByYearAndMonth(year, month);
        
        // Oyning kunlari sonini aniqlash
        int daysInMonth = LocalDateTime.of(year, month, 1, 0, 0).toLocalDate().lengthOfMonth();
        
        // Kunlik ma'lumotlarni tayyorlash
        Map<Integer, Long> dailyMap = new HashMap<>();
        for (int i = 1; i <= daysInMonth; i++) {
            dailyMap.put(i, 0L);
        }
        
        // Database'dan olingan ma'lumotlarni joylashtirish
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
        dailyStats.put("title", "Problems Solved in " + monthNames[month - 1] + " " + year);
        dailyStats.put("totalProblems", dailyMap.values().stream().mapToLong(Long::longValue).sum());
        
        return ResponseEntity.ok(dailyStats);
    }
}