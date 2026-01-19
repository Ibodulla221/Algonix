package com.code.algonix.problems;

import java.util.List;
import java.util.Map;

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
    @Operation(summary = "Barcha masalalarni olish", description = "Pagination, filter va search bilan barcha masalalar ro'yxati")
    public ResponseEntity<ProblemListResponse> getAllProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) Problem.Difficulty difficulty,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(problemService.getAllProblems(page, size, difficulty, categories, null, search));
    }

    @GetMapping("/user")
    @Operation(summary = "Foydalanuvchi uchun masalalar ro'yxati", description = "Yechilgan masalalar bilan birga, filter va search bilan")
    public ResponseEntity<ProblemListResponse> getProblemsForUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) Problem.Difficulty difficulty,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) String search,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        System.out.println("DEBUG Controller: Authentication: " + authentication + ", Username: " + username);
        return ResponseEntity.ok(problemService.getAllProblemsForUser(page, size, username, difficulty, categories, search));
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
    
    @PostMapping("/contest")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Contest uchun yangi masala yaratish", description = "Faqat ADMIN - Contest uchun maxsus masala")
    public ResponseEntity<Problem> createContestProblem(@RequestBody CreateProblemRequest request) {
        // Contest masalasi sifatida belgilash
        request.setIsContestOnly(true);
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
    public ResponseEntity<com.code.algonix.problems.dto.ProblemStatsResponse> getProblemStatistics(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(problemService.getProblemStatistics(username));
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
    
    @PostMapping("/{id}/favourite")
    @Operation(summary = "Masalani sevimlilar ro'yxatiga qo'shish/olib tashlash")
    public ResponseEntity<Map<String, Object>> toggleFavourite(
            @PathVariable Long id,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }
        
        String username = authentication.getName();
        boolean isFavourite = problemService.toggleFavourite(id, username);
        
        return ResponseEntity.ok(Map.of(
            "isFavourite", isFavourite,
            "message", isFavourite ? "Added to favourites" : "Removed from favourites"
        ));
    }
    
    @GetMapping("/favourites")
    @Operation(summary = "Sevimli masalalar ro'yxati va qidiruv")
    public ResponseEntity<ProblemListResponse> getFavouriteProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Problem.Difficulty difficulty,
            @RequestParam(required = false) List<String> categories,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        
        String username = authentication.getName();
        
        // If any search parameters are provided, use search method
        if ((search != null && !search.trim().isEmpty()) || 
            difficulty != null || 
            (categories != null && !categories.isEmpty())) {
            
            ProblemListResponse response = problemService.searchFavouriteProblems(
                username, search, difficulty, categories, page, size);
            return ResponseEntity.ok(response);
        } else {
            // Use simple method for backward compatibility
            List<ProblemListResponse.ProblemSummary> favourites = problemService.getFavouriteProblems(username, page, size);
            
            ProblemListResponse response = ProblemListResponse.builder()
                    .problems(favourites)
                    .page(page)
                    .pageSize(size)
                    .total((long) favourites.size())
                    .build();
            
            return ResponseEntity.ok(response);
        }
    }
}
