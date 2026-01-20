package com.code.algonix.problems;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.code.algonix.exception.ResourceNotFoundException;
import com.code.algonix.problems.dto.RunCodeRequest;
import com.code.algonix.problems.dto.RunCodeResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemServiceRunCode {

    private final ProblemRepository problemRepository;
    private final CodeExecutionServiceSelector codeExecutionServiceSelector;

    public RunCodeResponse runCode(Long problemId, RunCodeRequest request) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found: " + problemId));

        // Get only non-hidden test cases for run (not submit)
        List<TestCase> visibleTestCases = problem.getTestCases().stream()
                .filter(tc -> !tc.getIsHidden())
                .limit(3) // Only run first 3 visible test cases
                .toList();

        if (visibleTestCases.isEmpty()) {
            return RunCodeResponse.builder()
                    .status("error")
                    .output("")
                    .expectedOutput("")
                    .runtime(0)
                    .memory(0.0)
                    .passed(false)
                    .errorMessage("Test case'lar topilmadi")
                    .build();
        }

        // Execute code
        CodeExecutionService.ExecutionResult result = codeExecutionServiceSelector.executeCode(
                request.getCode(),
                request.getLanguage(),
                new ArrayList<>(visibleTestCases)
        );

        // Get first test case result
        if (result.getTestResults().isEmpty()) {
            return RunCodeResponse.builder()
                    .status("error")
                    .output("")
                    .expectedOutput("")
                    .runtime(0)
                    .memory(0.0)
                    .passed(false)
                    .errorMessage(result.getErrorMessage() != null ? result.getErrorMessage() : "Kod bajarilmadi")
                    .build();
        }

        CodeExecutionService.TestCaseResult firstResult = result.getTestResults().get(0);

        return RunCodeResponse.builder()
                .status(result.getStatus().name().toLowerCase())
                .output(firstResult.getActualOutput())
                .expectedOutput(firstResult.getExpectedOutput())
                .runtime(firstResult.getRuntime())
                .memory(firstResult.getMemory())
                .passed(firstResult.isPassed())
                .errorMessage(firstResult.getErrorMessage())
                .build();
    }
}
