package com.code.algonix.admin;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}