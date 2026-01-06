package com.code.algonix.admin;

import com.code.algonix.problems.Problem;
import com.code.algonix.problems.ProblemRepository;
import com.code.algonix.problems.ProblemService;
import com.code.algonix.problems.dto.CreateProblemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/problems")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProblemController {
    
    private final ProblemService problemService;
    private final ProblemRepository problemRepository;
    
    /**
     * Yangi masala yaratish
     */
    @PostMapping
    public ResponseEntity<Problem> createProblem(@RequestBody CreateProblemRequest request) {
        Problem problem = problemService.createProblem(request);
        return ResponseEntity.ok(problem);
    }
    
    /**
     * Masalani yangilash
     */
    @PutMapping("/{problemId}")
    public ResponseEntity<Problem> updateProblem(
            @PathVariable Long problemId,
            @RequestBody CreateProblemRequest request) {
        
        Problem existingProblem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        
        // Update fields
        existingProblem.setTitle(request.getTitle());
        existingProblem.setDescription(request.getDescription());
        existingProblem.setDifficulty(request.getDifficulty());
        existingProblem.setCategories(request.getCategories());
        existingProblem.setTags(request.getTags());
        existingProblem.setHints(request.getHints());
        // timeLimit va memoryLimit default qiymatlar bilan
        existingProblem.setTimeLimitMs(2000); // 2 seconds
        existingProblem.setMemoryLimitMb(256); // 256 MB
        
        Problem updatedProblem = problemRepository.save(existingProblem);
        return ResponseEntity.ok(updatedProblem);
    }
    
    /**
     * Masala statistikalarini olish
     */
    @GetMapping("/{problemId}/stats")
    public ResponseEntity<Map<String, Object>> getProblemStats(@PathVariable Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        
        Map<String, Object> stats = Map.of(
                "totalSubmissions", problem.getTotalSubmissions(),
                "totalAccepted", problem.getTotalAccepted(),
                "acceptanceRate", problem.getAcceptanceRate(),
                "likes", problem.getLikes(),
                "dislikes", problem.getDislikes(),
                "difficulty", problem.getDifficulty(),
                "categories", problem.getCategories()
        );
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Masalani publish/unpublish qilish
     */
    @PutMapping("/{problemId}/toggle-publish")
    public ResponseEntity<Map<String, String>> togglePublish(@PathVariable Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        
        // Bu yerda publish/unpublish logic qo'shish kerak
        // Hozircha message qaytaramiz
        
        return ResponseEntity.ok(Map.of(
                "message", "Problem publish status toggled",
                "problemId", problemId.toString()
        ));
    }
}