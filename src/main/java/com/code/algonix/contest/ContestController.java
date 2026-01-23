package com.code.algonix.contest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.code.algonix.contest.dto.ContestFinalResultsResponse;
import com.code.algonix.contest.dto.ContestRankingsResponse;
import com.code.algonix.contest.dto.ContestParticipantResponse;
import com.code.algonix.contest.dto.ContestProblemResponse;
import com.code.algonix.contest.dto.ContestResponse;
import com.code.algonix.contest.dto.ContestStandingsResponse;
import com.code.algonix.contest.dto.ContestSubmitRequest;
import com.code.algonix.contest.dto.CreateContestRequest;
import com.code.algonix.user.UserEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
    
    @GetMapping("/{contestId}/participants")
    @Operation(summary = "Get all contest participants")
    public ResponseEntity<List<ContestParticipantResponse>> getContestParticipants(
            @PathVariable Long contestId) {
        return ResponseEntity.ok(contestService.getContestParticipants(contestId));
    }
    
    @GetMapping("/{contestId}/rankings")
    @Operation(summary = "Get contest rankings (live or final)")
    public ResponseEntity<ContestRankingsResponse> getContestRankings(
            @PathVariable Long contestId) {
        return ResponseEntity.ok(contestService.getContestRankings(contestId));
    }
    
    @PostMapping("/{contestId}/finalize")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Finalize contest and calculate ratings", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> finalizeContest(@PathVariable Long contestId) {
        contestService.finalizeContest(contestId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{contestId}/finalize-with-results")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Finalize contest and get detailed results", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ContestFinalResultsResponse> finalizeContestWithResults(@PathVariable Long contestId) {
        return ResponseEntity.ok(contestService.finalizeContestWithResults(contestId));
    }
}
