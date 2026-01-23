package com.code.algonix.problems;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code.algonix.exception.ResourceNotFoundException;
import com.code.algonix.gamification.RewardResult;
import com.code.algonix.gamification.RewardService;
import com.code.algonix.problems.dto.SubmissionRequest;
import com.code.algonix.problems.dto.SubmissionResponse;
import com.code.algonix.user.UserEntity;
import com.code.algonix.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final RewardService rewardService;
    private final LeetCodeExecutionService leetCodeExecutionService;

    @Transactional
    public SubmissionResponse submitCode(SubmissionRequest request, String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));

        // Create submission
        Submission submission = Submission.builder()
                .user(user)
                .problem(problem)
                .code(request.getCode())
                .language(request.getLanguage())
                .status(Submission.SubmissionStatus.PENDING)
                .testResults(new ArrayList<>())
                .build();

        submission = submissionRepository.save(submission);

        // Execute code synchronously (can be made async later)
        RewardResult rewardResult = executeCode(submission, problem);

        SubmissionResponse response = mapToSubmissionResponse(submission);
        
        // Add reward info if available
        if (rewardResult != null) {
            response.setRewardInfo(SubmissionResponse.RewardInfo.builder()
                    .coinsEarned(rewardResult.getCoinsEarned())
                    .xpEarned(rewardResult.getXpEarned())
                    .leveledUp(rewardResult.isLeveledUp())
                    .oldLevel(rewardResult.getOldLevel())
                    .newLevel(rewardResult.getNewLevel())
                    .totalCoins(rewardResult.getTotalCoins())
                    .totalXp(rewardResult.getTotalXp())
                    .currentLevelXp(rewardResult.getCurrentLevelXp())
                    .xpToNextLevel(rewardResult.getXpToNextLevel())
                    .message(rewardResult.getMessage())
                    .build());
        }

        return response;
    }

    private RewardResult executeCode(Submission submission, Problem problem) {
        RewardResult rewardResult = null;
        try {
            // LeetCode style kod bajarish
            CodeExecutionService.ExecutionResult executionResult = leetCodeExecutionService.executeCode(
                    submission.getCode(),
                    submission.getLanguage(),
                    problem.getTestCases()
            );

            // Map execution results to test results
            List<TestResult> testResults = new ArrayList<>();
            for (CodeExecutionService.TestCaseResult tcResult : executionResult.getTestResults()) {
                TestCase testCase = problem.getTestCases().stream()
                        .filter(tc -> tc.getId().equals(tcResult.getTestCaseId()))
                        .findFirst()
                        .orElse(null);

                TestResult.TestStatus status = switch (tcResult.getStatus()) {
                    case ACCEPTED -> TestResult.TestStatus.PASSED;
                    case WRONG_ANSWER -> TestResult.TestStatus.FAILED;
                    case TIME_LIMIT_EXCEEDED -> TestResult.TestStatus.TIME_LIMIT_EXCEEDED;
                    case RUNTIME_ERROR -> TestResult.TestStatus.RUNTIME_ERROR;
                    default -> TestResult.TestStatus.FAILED;
                };

                TestResult result = TestResult.builder()
                        .submission(submission)
                        .testCase(testCase)
                        .status(status)
                        .runtime(tcResult.getRuntime())
                        .memory(tcResult.getMemory())
                        .input(tcResult.getInput())
                        .expectedOutput(tcResult.getExpectedOutput())
                        .actualOutput(tcResult.getActualOutput())
                        .errorMessage(tcResult.getErrorMessage())
                        .build();
                testResults.add(result);
            }

            // Set submission results
            submission.setTestResults(testResults);
            submission.setTotalTestCases(executionResult.getTotalTestCases());
            submission.setPassedTestCases(executionResult.getPassedTestCases());
            submission.setRuntime(executionResult.getAverageRuntime());
            submission.setMemory(executionResult.getAverageMemory());
            submission.setJudgedAt(LocalDateTime.now());

            // Determine submission status
            Submission.SubmissionStatus submissionStatus = switch (executionResult.getStatus()) {
                case ACCEPTED -> Submission.SubmissionStatus.ACCEPTED;
                case WRONG_ANSWER -> Submission.SubmissionStatus.WRONG_ANSWER;
                case TIME_LIMIT_EXCEEDED -> Submission.SubmissionStatus.TIME_LIMIT_EXCEEDED;
                case MEMORY_LIMIT_EXCEEDED -> Submission.SubmissionStatus.MEMORY_LIMIT_EXCEEDED;
                case RUNTIME_ERROR -> Submission.SubmissionStatus.RUNTIME_ERROR;
                case COMPILE_ERROR -> Submission.SubmissionStatus.COMPILE_ERROR;
            };
            submission.setStatus(submissionStatus);

            if (executionResult.getErrorMessage() != null) {
                submission.setErrorMessage(executionResult.getErrorMessage());
            }

            // Calculate percentiles (mock for now)
            submission.setRuntimePercentile(85.2);
            submission.setMemoryPercentile(72.5);

            // Save submission first to get ID
            submissionRepository.save(submission);

            // Process rewards if submission is accepted (AFTER saving submission)
            if (submissionStatus == Submission.SubmissionStatus.ACCEPTED) {
                rewardResult = rewardService.processSuccessfulSubmission(
                    submission.getUser(), 
                    problem, 
                    submission
                );
            }

        } catch (Exception e) {
            submission.setStatus(Submission.SubmissionStatus.RUNTIME_ERROR);
            submission.setErrorMessage("Kod bajarishda xato: " + e.getMessage());
            submission.setJudgedAt(LocalDateTime.now());
            submissionRepository.save(submission);
        }

        return rewardResult;
    }

    public SubmissionResponse getSubmission(Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        return mapToSubmissionResponse(submission);
    }

    public List<SubmissionResponse> getUserSubmissions(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return submissionRepository.findByUserIdOrderBySubmittedAtDesc(user.getId())
                .stream()
                .map(this::mapToSubmissionResponse)
                .collect(Collectors.toList());
    }

    private SubmissionResponse mapToSubmissionResponse(Submission submission) {
        List<SubmissionResponse.TestResultDto> testResults = submission.getTestResults().stream()
                .map(tr -> SubmissionResponse.TestResultDto.builder()
                        .testCaseId(tr.getTestCase() != null ? tr.getTestCase().getId() : null)
                        .status(tr.getStatus().name())
                        .runtime(tr.getRuntime())
                        .memory(tr.getMemory())
                        .input(tr.getInput())
                        .expectedOutput(tr.getExpectedOutput())
                        .actualOutput(tr.getActualOutput())
                        .errorMessage(tr.getErrorMessage())
                        .build())
                .collect(Collectors.toList());

        SubmissionResponse.OverallStats stats = SubmissionResponse.OverallStats.builder()
                .totalTestCases(submission.getTotalTestCases())
                .passedTestCases(submission.getPassedTestCases())
                .runtime(submission.getRuntime())
                .runtimePercentile(submission.getRuntimePercentile())
                .memory(submission.getMemory())
                .memoryPercentile(submission.getMemoryPercentile())
                .build();

        return SubmissionResponse.builder()
                .submissionId(submission.getId())
                .userId(submission.getUser().getId())
                .problemId(submission.getProblem().getId())
                .code(submission.getCode())
                .language(submission.getLanguage())
                .status(submission.getStatus())
                .testResults(testResults)
                .overallStats(stats)
                .submittedAt(submission.getSubmittedAt())
                .judgedAt(submission.getJudgedAt())
                .build();
    }
}
