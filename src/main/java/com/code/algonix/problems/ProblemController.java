package com.code.algonix.problems;

import com.code.algonix.problems.dto.CreateProblemRequest;
import com.code.algonix.problems.dto.ProblemDetailResponse;
import com.code.algonix.problems.dto.ProblemListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
@Tag(name = "Problems", description = "Masalalar bilan ishlovchi API")
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping
    @Operation(summary = "Barcha masalalarni olish", description = "Pagination bilan barcha masalalar ro'yxati")
    public ResponseEntity<ProblemListResponse> getAllProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(problemService.getAllProblems(page, size));
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
}
