package com.code.algonix.problems;

import com.code.algonix.exception.ResourceNotFoundException;
import com.code.algonix.problems.dto.SubmissionRequest;
import com.code.algonix.problems.dto.SubmissionResponse;
import com.code.algonix.user.UserEntity;
import com.code.algonix.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;

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

        // TODO: Async code execution with Docker
        // For now, just return pending status
        executeCode(submission, problem);

        return mapToSubmissionResponse(submission);
    }

    private void executeCode(Submission submission, Problem problem) {
        // TODO: Implement actual code execution logic
        // This is a placeholder
        List<TestResult> results = new ArrayList<>();
        int passed = 0;

        for (TestCase testCase : problem.getTestCases()) {
            TestResult result = TestResult.builder()
                    .submission(submission)
                    .testCase(testCase)
                    .status(TestResult.TestStatus.PASSED) // Mock
                    .runtime(45) // Mock
                    .memory(14.2) // Mock
                    .input(testCase.getInput())
                    .expectedOutput(testCase.getExpectedOutput())
                    .actualOutput(testCase.getExpectedOutput()) // Mock
                    .build();
            results.add(result);
            passed++;
        }

        submission.setTestResults(results);
        submission.setTotalTestCases(problem.getTestCases().size());
        submission.setPassedTestCases(passed);
        submission.setStatus(Submission.SubmissionStatus.ACCEPTED);
        submission.setRuntime(45);
        submission.setRuntimePercentile(85.2);
        submission.setMemory(14.2);
        submission.setMemoryPercentile(72.5);
        submission.setJudgedAt(LocalDateTime.now());

        submissionRepository.save(submission);
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
