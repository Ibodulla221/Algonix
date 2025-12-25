package com.code.algonix.problems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code.algonix.exception.ResourceNotFoundException;
import com.code.algonix.problems.dto.CreateProblemRequest;
import com.code.algonix.problems.dto.ProblemDetailResponse;
import com.code.algonix.problems.dto.ProblemListResponse;
import com.code.algonix.user.UserEntity;
import com.code.algonix.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemServiceRunCode runCodeService;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Problem createProblem(CreateProblemRequest request) {
        Problem problem = Problem.builder()
                .slug(request.getSlug())
                .title(request.getTitle())
                .difficulty(request.getDifficulty())
                .categories(request.getCategories())
                .tags(request.getTags())
                .description(request.getDescription())
                .descriptionHtml(request.getDescriptionHtml())
                .constraints(request.getConstraints())
                .hints(request.getHints())
                .relatedProblems(request.getRelatedProblems())
                .companies(request.getCompanies())
                .frequency(request.getFrequency())
                .isPremium(request.getIsPremium())
                .build();

        // Add examples
        if (request.getExamples() != null) {
            List<ProblemExample> examples = request.getExamples().stream()
                    .map(ex -> ProblemExample.builder()
                            .problem(problem)
                            .caseNumber(ex.getCaseNumber())
                            .input(ex.getInput())
                            .target(ex.getTarget())
                            .output(ex.getOutput())
                            .explanation(ex.getExplanation())
                            .build())
                    .collect(Collectors.toList());
            problem.setExamples(examples);
        }

        // Add code templates
        if (request.getCodeTemplates() != null) {
            List<CodeTemplate> templates = request.getCodeTemplates().entrySet().stream()
                    .map(entry -> CodeTemplate.builder()
                            .problem(problem)
                            .language(entry.getKey())
                            .code(entry.getValue())
                            .build())
                    .collect(Collectors.toList());
            problem.setCodeTemplates(templates);
        }

        // Add test cases
        if (request.getTestCases() != null) {
            List<TestCase> testCases = request.getTestCases().stream()
                    .map(tc -> TestCase.builder()
                            .problem(problem)
                            .input(tc.getInput())
                            .expectedOutput(tc.getExpectedOutput())
                            .isHidden(tc.getIsHidden() != null && tc.getIsHidden())
                            .timeLimitMs(Objects.requireNonNullElse(tc.getTimeLimitMs(), 2000))
                            .build())
                    .collect(Collectors.toList());
            problem.setTestCases(testCases);
        }

        return problemRepository.save(problem);
    }

    public ProblemListResponse getAllProblems(int page, int size) {
        return getAllProblems(page, size, null, null);
    }

    public ProblemListResponse getAllProblems(int page, int size, Problem.Difficulty difficulty, List<String> categories) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Problem> problemPage;
        
        // Use appropriate filtering method based on parameters
        if (difficulty != null && categories != null && !categories.isEmpty()) {
            problemPage = problemRepository.findByDifficultyAndCategories(difficulty, categories, pageable);
        } else if (difficulty != null) {
            problemPage = problemRepository.findByDifficulty(difficulty, pageable);
        } else if (categories != null && !categories.isEmpty()) {
            problemPage = problemRepository.findByCategories(categories, pageable);
        } else {
            problemPage = problemRepository.findAll(pageable);
        }

        // Calculate sequence numbers based on global sequence number
        List<ProblemListResponse.ProblemSummary> summaries = new ArrayList<>();
        for (int i = 0; i < problemPage.getContent().size(); i++) {
            Problem p = problemPage.getContent().get(i);
            
            ProblemListResponse.ProblemSummary summary = ProblemListResponse.ProblemSummary.builder()
                    .sequenceNumber(p.getGlobalSequenceNumber() != null ? p.getGlobalSequenceNumber() : (page * size) + i + 1)
                    .id(p.getId())
                    .slug(p.getSlug())
                    .title(p.getTitle())
                    .difficulty(p.getDifficulty())
                    .acceptanceRate(p.getAcceptanceRate())
                    .isPremium(p.getIsPremium())
                    .frequency(p.getFrequency())
                    .categories(p.getCategories())
                    .status("todo") // Will be calculated on frontend based on user submissions
                    .timeLimitMs(p.getTimeLimitMs() != null ? p.getTimeLimitMs() : 2000)
                    .memoryLimitMb(p.getMemoryLimitMb() != null ? p.getMemoryLimitMb() : 512)
                    .build();
            summaries.add(summary);
        }

        return ProblemListResponse.builder()
                .total(problemPage.getTotalElements())
                .page(page)
                .pageSize(size)
                .problems(summaries)
                .build();
    }

    public ProblemListResponse getAllProblemsForUser(int page, int size, String username) {
        return getAllProblemsForUser(page, size, username, null, null);
    }

    public ProblemListResponse getAllProblemsForUser(int page, int size, String username, 
                                                   Problem.Difficulty difficulty, List<String> categories) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Problem> problemPage;
        
        // Use appropriate filtering method based on parameters
        if (difficulty != null && categories != null && !categories.isEmpty()) {
            problemPage = problemRepository.findByDifficultyAndCategories(difficulty, categories, pageable);
        } else if (difficulty != null) {
            problemPage = problemRepository.findByDifficulty(difficulty, pageable);
        } else if (categories != null && !categories.isEmpty()) {
            problemPage = problemRepository.findByCategories(categories, pageable);
        } else {
            problemPage = problemRepository.findAll(pageable);
        }

        // Get user to check solved problems
        UserEntity user = null;
        if (username != null) {
            user = userRepository.findByUsername(username).orElse(null);
            System.out.println("DEBUG: Username: " + username + ", User found: " + (user != null));
            if (user != null) {
                System.out.println("DEBUG: User ID: " + user.getId() + ", Username: " + user.getUsername());
            }
        } else {
            System.out.println("DEBUG: Username is null");
        }

        final UserEntity finalUser = user;
        List<ProblemListResponse.ProblemSummary> summaries = new ArrayList<>();
        
        for (int i = 0; i < problemPage.getContent().size(); i++) {
            Problem p = problemPage.getContent().get(i);
            
            String status = "todo"; // Default status
            
            if (finalUser != null) {
                // Check if user has solved this problem
                boolean hasSolved = submissionRepository.existsByUserAndProblemAndStatus(
                    finalUser, p, Submission.SubmissionStatus.ACCEPTED
                );
                status = hasSolved ? "solved" : "todo";
                
                // Debug log
                if (p.getId().equals(6L)) {
                    System.out.println("DEBUG: Problem 6 check for user " + finalUser.getUsername() + 
                                     " - hasSolved: " + hasSolved);
                }
            }
            
            ProblemListResponse.ProblemSummary summary = ProblemListResponse.ProblemSummary.builder()
                    .sequenceNumber(p.getGlobalSequenceNumber() != null ? p.getGlobalSequenceNumber() : (page * size) + i + 1)
                    .id(p.getId())
                    .slug(p.getSlug())
                    .title(p.getTitle())
                    .difficulty(p.getDifficulty())
                    .acceptanceRate(p.getAcceptanceRate())
                    .isPremium(p.getIsPremium())
                    .frequency(p.getFrequency())
                    .categories(p.getCategories())
                    .status(status)
                    .timeLimitMs(p.getTimeLimitMs() != null ? p.getTimeLimitMs() : 2000)
                    .memoryLimitMb(p.getMemoryLimitMb() != null ? p.getMemoryLimitMb() : 512)
                    .build();
            summaries.add(summary);
        }

        return ProblemListResponse.builder()
                .total(problemPage.getTotalElements())
                .page(page)
                .pageSize(size)
                .problems(summaries)
                .build();
    }

    public ProblemDetailResponse getProblemBySlug(String slug) {
        Problem problem = problemRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found: " + slug));

        return mapToProblemDetailResponse(problem);
    }

    public ProblemDetailResponse getProblemById(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found: " + id));

        return mapToProblemDetailResponse(problem);
    }

    private ProblemDetailResponse mapToProblemDetailResponse(Problem problem) {
        Map<String, String> codeTemplates = new HashMap<>();
        if (problem.getCodeTemplates() != null) {
            problem.getCodeTemplates().forEach(ct ->
                    codeTemplates.put(ct.getLanguage(), ct.getCode()));
        }

        List<ProblemDetailResponse.ExampleDto> examples = problem.getExamples().stream()
                .map(ex -> ProblemDetailResponse.ExampleDto.builder()
                        .id(ex.getId())
                        .caseNumber(ex.getCaseNumber())
                        .input(ex.getInput())
                        .target(ex.getTarget())
                        .output(ex.getOutput())
                        .explanation(ex.getExplanation())
                        .build())
                .collect(Collectors.toList());

        return ProblemDetailResponse.builder()
                .id(problem.getId())
                .slug(problem.getSlug())
                .title(problem.getTitle())
                .difficulty(problem.getDifficulty())
                .categories(problem.getCategories())
                .tags(problem.getTags())
                .likes(problem.getLikes())
                .dislikes(problem.getDislikes())
                .acceptanceRate(problem.getAcceptanceRate())
                .totalSubmissions(problem.getTotalSubmissions())
                .totalAccepted(problem.getTotalAccepted())
                .description(problem.getDescription())
                .descriptionHtml(problem.getDescriptionHtml())
                .examples(examples)
                .constraints(problem.getConstraints())
                .hints(problem.getHints())
                .codeTemplates(codeTemplates)
                .relatedProblems(problem.getRelatedProblems())
                .companies(problem.getCompanies())
                .frequency(problem.getFrequency())
                .isPremium(problem.getIsPremium())
                .timeLimitMs(problem.getTimeLimitMs() != null ? problem.getTimeLimitMs() : 2000)
                .memoryLimitMb(problem.getMemoryLimitMb() != null ? problem.getMemoryLimitMb() : 512)
                .createdAt(problem.getCreatedAt())
                .updatedAt(problem.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deleteProblem(Long id) {
        if (!problemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Problem not found: " + id);
        }
        problemRepository.deleteById(id);
    }

    public com.code.algonix.problems.dto.RunCodeResponse runCode(Long problemId, com.code.algonix.problems.dto.RunCodeRequest request) {
        return runCodeService.runCode(problemId, request);
    }

    public com.code.algonix.problems.dto.ProblemStatsResponse getProblemStatistics(String username) {
        // Get total count
        Long totalProblems = problemRepository.count();
        
        // Get count by each difficulty
        Long beginnerCount = problemRepository.countByDifficulty(Problem.Difficulty.BEGINNER);
        Long basicCount = problemRepository.countByDifficulty(Problem.Difficulty.BASIC);
        Long normalCount = problemRepository.countByDifficulty(Problem.Difficulty.NORMAL);
        Long mediumCount = problemRepository.countByDifficulty(Problem.Difficulty.MEDIUM);
        Long hardCount = problemRepository.countByDifficulty(Problem.Difficulty.HARD);
        
        com.code.algonix.problems.dto.ProblemStatsResponse.DifficultyStats difficultyStats = 
            com.code.algonix.problems.dto.ProblemStatsResponse.DifficultyStats.builder()
                .beginner(beginnerCount)
                .basic(basicCount)
                .normal(normalCount)
                .medium(mediumCount)
                .hard(hardCount)
                .build();
        
        // Get user statistics if username provided
        Long totalSolved = 0L;
        com.code.algonix.problems.dto.ProblemStatsResponse.DifficultyUserStats difficultyUserStats = null;
        
        if (username != null) {
            UserEntity user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                totalSolved = submissionRepository.countSolvedProblemsByUser(user);
                
                Long userBeginnerCount = submissionRepository.countSolvedProblemsByUserAndDifficulty(user, Problem.Difficulty.BEGINNER);
                Long userBasicCount = submissionRepository.countSolvedProblemsByUserAndDifficulty(user, Problem.Difficulty.BASIC);
                Long userNormalCount = submissionRepository.countSolvedProblemsByUserAndDifficulty(user, Problem.Difficulty.NORMAL);
                Long userMediumCount = submissionRepository.countSolvedProblemsByUserAndDifficulty(user, Problem.Difficulty.MEDIUM);
                Long userHardCount = submissionRepository.countSolvedProblemsByUserAndDifficulty(user, Problem.Difficulty.HARD);
                
                difficultyUserStats = com.code.algonix.problems.dto.ProblemStatsResponse.DifficultyUserStats.builder()
                        .beginner(userBeginnerCount)
                        .basic(userBasicCount)
                        .normal(userNormalCount)
                        .medium(userMediumCount)
                        .hard(userHardCount)
                        .build();
            }
        }
        
        return com.code.algonix.problems.dto.ProblemStatsResponse.builder()
                .totalProblems(totalProblems)
                .difficultyStats(difficultyStats)
                .totalSolved(totalSolved)
                .difficultyUserStats(difficultyUserStats)
                .build();
    }

    public com.code.algonix.problems.dto.CategoryStatsResponse getCategoryStatistics() {
        // Get total count
        Long totalProblems = problemRepository.count();
        
        // Get count by each category
        List<Object[]> categoryResults = problemRepository.countByCategory();
        Map<String, Long> categoryStats = new HashMap<>();
        
        for (Object[] result : categoryResults) {
            String category = (String) result[0];
            Long count = (Long) result[1];
            categoryStats.put(category, count);
        }
        
        return com.code.algonix.problems.dto.CategoryStatsResponse.builder()
                .totalProblems(totalProblems)
                .categoryStats(categoryStats)
                .build();
    }
}
