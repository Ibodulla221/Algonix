package com.code.algonix.contest;

import com.code.algonix.contest.dto.*;
import com.code.algonix.user.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
@Tag(name = "Contest", description = "Contest management APIs")
public class ContestController {
    
    private final ContestService contestService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new contest", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ContestResponse> createContest(@Valid @RequestBody CreateContestRequest request) {
        return ResponseEntity.ok(contestService.createContest(request));
    }
    
    @GetMapping
    @Operation(summary = "Get all contests")
    public ResponseEntity<List<ContestResponse>> getAllContests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal UserEntity user) {
        Long userId = user != null ? user.getId() : null;
        return ResponseEntity.ok(contestService.getAllContests(page, size, userId));
    }
    
    @GetMapping("/{contestId}")
    @Operation(summary = "Get contest by ID")
    public ResponseEntity<ContestResponse> getContestById(
            @PathVariable Long contestId,
            @AuthenticationPrincipal UserEntity user) {
        Long userId = user != null ? user.getId() : null;
        return ResponseEntity.ok(contestService.getContestById(contestId, userId));
    }
    
    @PostMapping("/{contestId}/register")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Register for contest", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> registerForContest(
            @PathVariable Long contestId,
            @AuthenticationPrincipal UserEntity user) {
        contestService.registerForContest(contestId, user.getId());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{contestId}/problems")
    @Operation(summary = "Get contest problems")
    public ResponseEntity<List<ContestProblemResponse>> getContestProblems(
            @PathVariable Long contestId,
            @AuthenticationPrincipal UserEntity user) {
        Long userId = user != null ? user.getId() : null;
        return ResponseEntity.ok(contestService.getContestProblems(contestId, userId));
    }
    
    @PostMapping("/submit")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Submit solution to contest problem", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> submitSolution(
            @Valid @RequestBody ContestSubmitRequest request,
            @AuthenticationPrincipal UserEntity user) {
        contestService.submitSolution(request, user.getId());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{contestId}/standings")
    @Operation(summary = "Get contest standings")
    public ResponseEntity<List<ContestStandingsResponse>> getContestStandings(
            @PathVariable Long contestId) {
        return ResponseEntity.ok(contestService.getContestStandings(contestId));
    }
    
    @PostMapping("/{contestId}/finalize")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Finalize contest and calculate ratings", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> finalizeContest(@PathVariable Long contestId) {
        contestService.finalizeContest(contestId);
        return ResponseEntity.ok().build();
    }
}
