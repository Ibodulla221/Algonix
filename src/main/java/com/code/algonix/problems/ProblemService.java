package com.code.algonix.problems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    private final FavouriteRepository favouriteRepository;
    private final CodeTemplateService codeTemplateService;

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
                .isContestOnly(request.getIsContestOnly())
                // contestId ni olib tashladik
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
        return getAllProblems(page, size, difficulty, categories, null, null);
    }
    
    public ProblemListResponse getAllProblems(int page, int size, Problem.Difficulty difficulty, List<String> categories, String username) {
        return getAllProblems(page, size, difficulty, categories, username, null);
    }
    
    public ProblemListResponse getAllProblems(int page, int size, Problem.Difficulty difficulty, List<String> categories, String username, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Problem> problemPage;
        
        // Check if search is for sequence number (numeric)
        boolean isSequenceSearch = false;
        Integer sequenceNumber = null;
        if (search != null && !search.trim().isEmpty()) {
            try {
                sequenceNumber = Integer.parseInt(search.trim());
                isSequenceSearch = true;
            } catch (NumberFormatException e) {
                // Not a number, search by title
                isSequenceSearch = false;
            }
        }
        
        // Use appropriate filtering method based on parameters
        if (search != null && !search.trim().isEmpty()) {
            if (isSequenceSearch) {
                // Search by sequence number
                problemPage = problemRepository.findByGlobalSequenceNumber(sequenceNumber, pageable);
            } else {
                // Search by title with filters
                if (difficulty != null && categories != null && !categories.isEmpty()) {
                    problemPage = problemRepository.findByTitleContainingIgnoreCaseAndDifficultyAndCategories(search, difficulty, categories, pageable);
                } else if (difficulty != null) {
                    problemPage = problemRepository.findByTitleContainingIgnoreCaseAndDifficulty(search, difficulty, pageable);
                } else if (categories != null && !categories.isEmpty()) {
                    problemPage = problemRepository.findByTitleContainingIgnoreCaseAndCategories(search, categories, pageable);
                } else {
                    problemPage = problemRepository.findByTitleContainingIgnoreCase(search, pageable);
                }
            }
        } else {
            // No search, use existing filter logic
            if (difficulty != null && categories != null && !categories.isEmpty()) {
                problemPage = problemRepository.findByDifficultyAndCategories(difficulty, categories, pageable);
            } else if (difficulty != null) {
                problemPage = problemRepository.findByDifficulty(difficulty, pageable);
            } else if (categories != null && !categories.isEmpty()) {
                problemPage = problemRepository.findByCategories(categories, pageable);
            } else {
                problemPage = problemRepository.findAll(pageable);
            }
        }

        // Get user for favourite checking
        UserEntity user = null;
        if (username != null) {
            user = userRepository.findByUsername(username).orElse(null);
        }

        // Calculate sequence numbers based on global sequence number
        List<ProblemListResponse.ProblemSummary> summaries = new ArrayList<>();
        final UserEntity finalUser = user;
        
        for (int i = 0; i < problemPage.getContent().size(); i++) {
            Problem p = problemPage.getContent().get(i);
            
            // Check if favourited
            boolean isFavourite = false;
            if (finalUser != null) {
                isFavourite = favouriteRepository.existsByUserAndProblem(finalUser, p);
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
                    .status("todo") // Will be calculated on frontend based on user submissions
                    .isFavourite(isFavourite)
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
        return getAllProblemsForUser(page, size, username, difficulty, categories, null);
    }

    public ProblemListResponse getAllProblemsForUser(int page, int size, String username, 
                                                   Problem.Difficulty difficulty, List<String> categories, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Problem> problemPage;
        
        // Check if search is for sequence number (numeric)
        boolean isSequenceSearch = false;
        Integer sequenceNumber = null;
        if (search != null && !search.trim().isEmpty()) {
            try {
                sequenceNumber = Integer.parseInt(search.trim());
                isSequenceSearch = true;
            } catch (NumberFormatException e) {
                // Not a number, search by title
                isSequenceSearch = false;
            }
        }
        
        // Use appropriate filtering method based on parameters
        if (search != null && !search.trim().isEmpty()) {
            if (isSequenceSearch) {
                // Search by sequence number
                problemPage = problemRepository.findByGlobalSequenceNumber(sequenceNumber, pageable);
            } else {
                // Search by title with filters
                if (difficulty != null && categories != null && !categories.isEmpty()) {
                    problemPage = problemRepository.findByTitleContainingIgnoreCaseAndDifficultyAndCategories(search, difficulty, categories, pageable);
                } else if (difficulty != null) {
                    problemPage = problemRepository.findByTitleContainingIgnoreCaseAndDifficulty(search, difficulty, pageable);
                } else if (categories != null && !categories.isEmpty()) {
                    problemPage = problemRepository.findByTitleContainingIgnoreCaseAndCategories(search, categories, pageable);
                } else {
                    problemPage = problemRepository.findByTitleContainingIgnoreCase(search, pageable);
                }
            }
        } else {
            // No search, use existing filter logic
            if (difficulty != null && categories != null && !categories.isEmpty()) {
                problemPage = problemRepository.findByDifficultyAndCategories(difficulty, categories, pageable);
            } else if (difficulty != null) {
                problemPage = problemRepository.findByDifficulty(difficulty, pageable);
            } else if (categories != null && !categories.isEmpty()) {
                problemPage = problemRepository.findByCategories(categories, pageable);
            } else {
                problemPage = problemRepository.findAll(pageable);
            }
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
            boolean isFavourite = false; // Default favourite status
            
            if (finalUser != null) {
                // Check if user has solved this problem
                boolean hasSolved = submissionRepository.existsByUserAndProblemAndStatus(
                    finalUser, p, Submission.SubmissionStatus.ACCEPTED
                );
                status = hasSolved ? "solved" : "todo";
                
                // Check if user has favourited this problem
                isFavourite = favouriteRepository.existsByUserAndProblem(finalUser, p);
                
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
                    .isFavourite(isFavourite)
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
        // Code template'larni olish
        List<CodeTemplate> templates = codeTemplateService.getTemplatesForProblem(problem.getId());
        Map<String, String> codeTemplates = templates.stream()
            .collect(Collectors.toMap(
                CodeTemplate::getLanguage,
                CodeTemplate::getCode
            ));

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
        
        // Get user statistics if username provided
        Long totalSolved = 0L;
        Long userBeginnerCount = 0L;
        Long userBasicCount = 0L;
        Long userNormalCount = 0L;
        Long userMediumCount = 0L;
        Long userHardCount = 0L;
        
        if (username != null) {
            UserEntity user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                totalSolved = submissionRepository.countSolvedProblemsByUser(user);
                userBeginnerCount = submissionRepository.countSolvedProblemsByUserAndDifficulty(user, Problem.Difficulty.BEGINNER);
                userBasicCount = submissionRepository.countSolvedProblemsByUserAndDifficulty(user, Problem.Difficulty.BASIC);
                userNormalCount = submissionRepository.countSolvedProblemsByUserAndDifficulty(user, Problem.Difficulty.NORMAL);
                userMediumCount = submissionRepository.countSolvedProblemsByUserAndDifficulty(user, Problem.Difficulty.MEDIUM);
                userHardCount = submissionRepository.countSolvedProblemsByUserAndDifficulty(user, Problem.Difficulty.HARD);
            }
        }
        
        // Create difficulty stats list
        List<com.code.algonix.problems.dto.ProblemStatsResponse.DifficultyStatItem> difficultyStats = List.of(
            com.code.algonix.problems.dto.ProblemStatsResponse.DifficultyStatItem.builder()
                .name("Beginner")
                .total(beginnerCount)
                .solved(userBeginnerCount)
                .build(),
            com.code.algonix.problems.dto.ProblemStatsResponse.DifficultyStatItem.builder()
                .name("Basic")
                .total(basicCount)
                .solved(userBasicCount)
                .build(),
            com.code.algonix.problems.dto.ProblemStatsResponse.DifficultyStatItem.builder()
                .name("Normal")
                .total(normalCount)
                .solved(userNormalCount)
                .build(),
            com.code.algonix.problems.dto.ProblemStatsResponse.DifficultyStatItem.builder()
                .name("Medium")
                .total(mediumCount)
                .solved(userMediumCount)
                .build(),
            com.code.algonix.problems.dto.ProblemStatsResponse.DifficultyStatItem.builder()
                .name("Hard")
                .total(hardCount)
                .solved(userHardCount)
                .build()
        );
        
        return com.code.algonix.problems.dto.ProblemStatsResponse.builder()
                .allProblems(totalProblems)
                .allUserSolvedProblems(totalSolved)
                .difficultyStats(difficultyStats)
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
    
    @Transactional
    public boolean toggleFavourite(Long problemId, String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
        
        // Check if already favourited
        Optional<Favourite> existingFavourite = favouriteRepository.findByUserAndProblem(user, problem);
        
        if (existingFavourite.isPresent()) {
            // Remove from favourites
            favouriteRepository.delete(existingFavourite.get());
            return false; // Not favourited anymore
        } else {
            // Add to favourites
            Favourite favourite = Favourite.builder()
                    .user(user)
                    .problem(problem)
                    .build();
            favouriteRepository.save(favourite);
            return true; // Now favourited
        }
    }
    
    public List<ProblemListResponse.ProblemSummary> getFavouriteProblems(String username, int page, int size) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Favourite> favouritePage = favouriteRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        return favouritePage.getContent().stream()
                .map(fav -> {
                    Problem p = fav.getProblem();
                    
                    // Check if solved
                    boolean hasSolved = submissionRepository.existsByUserAndProblemAndStatus(
                        user, p, Submission.SubmissionStatus.ACCEPTED
                    );
                    String status = hasSolved ? "solved" : "todo";
                    
                    return ProblemListResponse.ProblemSummary.builder()
                            .sequenceNumber(p.getGlobalSequenceNumber())
                            .id(p.getId())
                            .slug(p.getSlug())
                            .title(p.getTitle())
                            .difficulty(p.getDifficulty())
                            .acceptanceRate(p.getAcceptanceRate())
                            .isPremium(p.getIsPremium())
                            .frequency(p.getFrequency())
                            .categories(p.getCategories())
                            .status(status)
                            .isFavourite(true) // All are favourites in this list
                            .timeLimitMs(p.getTimeLimitMs() != null ? p.getTimeLimitMs() : 2000)
                            .memoryLimitMb(p.getMemoryLimitMb() != null ? p.getMemoryLimitMb() : 512)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    public ProblemListResponse searchFavouriteProblems(
            String username, 
            String search, 
            Problem.Difficulty difficulty, 
            List<String> categories, 
            int page, 
            int size) {
        
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Favourite> favouritePage;
        
        // Apply filters based on provided parameters
        if (search != null && !search.trim().isEmpty()) {
            if (difficulty != null && categories != null && !categories.isEmpty()) {
                // Search + difficulty + categories
                favouritePage = favouriteRepository.findByUserAndProblemTitleContainingIgnoreCaseAndDifficultyAndCategories(
                    user, search.trim(), difficulty, categories, pageable);
            } else if (difficulty != null) {
                // Search + difficulty
                favouritePage = favouriteRepository.findByUserAndProblemTitleContainingIgnoreCaseAndDifficulty(
                    user, search.trim(), difficulty, pageable);
            } else if (categories != null && !categories.isEmpty()) {
                // Search + categories
                favouritePage = favouriteRepository.findByUserAndProblemTitleContainingIgnoreCaseAndCategories(
                    user, search.trim(), categories, pageable);
            } else {
                // Search only
                favouritePage = favouriteRepository.findByUserAndProblemTitleContainingIgnoreCase(
                    user, search.trim(), pageable);
            }
        } else {
            // No search term
            if (difficulty != null && categories != null && !categories.isEmpty()) {
                // Difficulty + categories
                favouritePage = favouriteRepository.findByUserAndProblemDifficultyAndCategories(
                    user, difficulty, categories, pageable);
            } else if (difficulty != null) {
                // Difficulty only
                favouritePage = favouriteRepository.findByUserAndProblemDifficulty(
                    user, difficulty, pageable);
            } else if (categories != null && !categories.isEmpty()) {
                // Categories only
                favouritePage = favouriteRepository.findByUserAndProblemCategories(
                    user, categories, pageable);
            } else {
                // No filters
                favouritePage = favouriteRepository.findByUserOrderByCreatedAtDesc(user, pageable);
            }
        }
        
        List<ProblemListResponse.ProblemSummary> problemSummaries = favouritePage.getContent().stream()
                .map(fav -> {
                    Problem p = fav.getProblem();
                    
                    // Check if solved
                    boolean hasSolved = submissionRepository.existsByUserAndProblemAndStatus(
                        user, p, Submission.SubmissionStatus.ACCEPTED
                    );
                    String status = hasSolved ? "solved" : "todo";
                    
                    return ProblemListResponse.ProblemSummary.builder()
                            .sequenceNumber(p.getGlobalSequenceNumber())
                            .id(p.getId())
                            .slug(p.getSlug())
                            .title(p.getTitle())
                            .difficulty(p.getDifficulty())
                            .acceptanceRate(p.getAcceptanceRate())
                            .isPremium(p.getIsPremium())
                            .frequency(p.getFrequency())
                            .categories(p.getCategories())
                            .status(status)
                            .isFavourite(true) // All are favourites in this list
                            .timeLimitMs(p.getTimeLimitMs() != null ? p.getTimeLimitMs() : 2000)
                            .memoryLimitMb(p.getMemoryLimitMb() != null ? p.getMemoryLimitMb() : 512)
                            .build();
                })
                .collect(Collectors.toList());
        
        return ProblemListResponse.builder()
                .problems(problemSummaries)
                .total(favouritePage.getTotalElements())
                .page(page)
                .pageSize(size)
                .build();
    }
}