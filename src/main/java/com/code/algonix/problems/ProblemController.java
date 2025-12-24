package com.code.algonix.problems;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.code.algonix.problems.dto.CreateProblemRequest;
import com.code.algonix.problems.dto.ProblemDetailResponse;
import com.code.algonix.problems.dto.ProblemListResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
@Tag(name = "Problems", description = "Masalalar bilan ishlovchi API")
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping
    @Operation(summary = "Barcha masalalarni olish", description = "Pagination va filter bilan barcha masalalar ro'yxati")
    public ResponseEntity<ProblemListResponse> getAllProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) Problem.Difficulty difficulty,
            @RequestParam(required = false) List<String> categories) {
        return ResponseEntity.ok(problemService.getAllProblems(page, size, difficulty, categories));
    }

    @GetMapping("/user")
    @Operation(summary = "Foydalanuvchi uchun masalalar ro'yxati", description = "Yechilgan masalalar bilan birga, filter bilan")
    public ResponseEntity<ProblemListResponse> getProblemsForUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) Problem.Difficulty difficulty,
            @RequestParam(required = false) List<String> categories,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        System.out.println("DEBUG Controller: Authentication: " + authentication + ", Username: " + username);
        return ResponseEntity.ok(problemService.getAllProblemsForUser(page, size, username, difficulty, categories));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Masalani ID bo'yicha olish")
    public ResponseEntity<ProblemDetailResponse> getProblemById(@PathVariable Long id) {
        return ResponseEntity.ok(problemService.getProblemById(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Masalani slug bo'yicha olish")
    public ResponseEntity<ProblemDetailResponse> getProblemBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(problemService.getProblemBySlug(slug));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Yangi masala yaratish", description = "Faqat ADMIN")
    public ResponseEntity<Problem> createProblem(@RequestBody CreateProblemRequest request) {
        return ResponseEntity.ok(problemService.createProblem(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Masalani o'chirish", description = "Faqat ADMIN")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @Operation(summary = "Masalalar statistikasi", description = "Jami masalalar soni va qiyinchilik darajasi bo'yicha statistika")
    public ResponseEntity<com.code.algonix.problems.dto.ProblemStatsResponse> getProblemStatistics() {
        return ResponseEntity.ok(problemService.getProblemStatistics());
    }

    @GetMapping("/stats/categories")
    @Operation(summary = "Kategoriyalar statistikasi", description = "Jami masalalar soni va kategoriyalar bo'yicha statistika")
    public ResponseEntity<com.code.algonix.problems.dto.CategoryStatsResponse> getCategoryStatistics() {
        return ResponseEntity.ok(problemService.getCategoryStatistics());
    }

    @PostMapping("/{id}/run")
    @Operation(summary = "Kodni test qilish", description = "Submit qilmasdan test run")
    public ResponseEntity<com.code.algonix.problems.dto.RunCodeResponse> runCode(
            @PathVariable Long id,
            @RequestBody com.code.algonix.problems.dto.RunCodeRequest request) {
        return ResponseEntity.ok(problemService.runCode(id, request));
    }
}
