package com.code.algonix.problems;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeExecutionService {

    private final DockerExecutionHelper dockerHelper;

    @Value("${code.execution.timeout-ms:5000}")
    private long timeoutMs;

    public ExecutionResult executeCode(String code, String language, List<TestCase> testCases) {
        Path workDir = null;
        try {
            // Create temporary directory
            workDir = Files.createTempDirectory("algonix-exec-");
            log.info("Created temp directory: {}", workDir);

            // Write source code to file
            String sourceFileName = dockerHelper.sourceFileNameFor(language);
            Path sourceFile = workDir.resolve(sourceFileName);
            Files.writeString(sourceFile, code);
            log.info("Wrote source code to: {}", sourceFile);

            // Compile if needed
            DockerExecutionHelper.ExecutionResult compileResult = dockerHelper.compileIfNeeded(language, workDir);
            if (!compileResult.isSuccess()) {
                log.error("Compilation failed: {}", compileResult.getStdErr());
                return ExecutionResult.builder()
                        .status(ExecutionStatus.COMPILE_ERROR)
                        .errorMessage(compileResult.getStdErr())
                        .testResults(new ArrayList<>())
                        .build();
            }

            // Run test cases
            List<TestCaseResult> testResults = new ArrayList<>();
            int passedCount = 0;
            long totalRuntime = 0;
            double totalMemory = 0;

            for (TestCase testCase : testCases) {
                try {
                    DockerExecutionHelper.ExecutionResult runResult = dockerHelper.runExecutable(
                            language,
                            workDir,
                            testCase.getInput(),
                            testCase.getTimeLimitMs() != null ? testCase.getTimeLimitMs() : timeoutMs
                    );

                    TestCaseResult result = evaluateTestCase(testCase, runResult);
                    testResults.add(result);

                    if (result.isPassed()) {
                        passedCount++;
                    }
                    totalRuntime += runResult.getTimeMs();
                    totalMemory += 14.2; // Mock memory usage

                    // Stop on first failure for hidden test cases
                    if (!result.isPassed() && testCase.getIsHidden()) {
                        break;
                    }
                } catch (Exception e) {
                    log.error("Error executing test case: {}", e.getMessage());
                    testResults.add(TestCaseResult.builder()
                            .testCaseId(testCase.getId())
                            .status(ExecutionStatus.RUNTIME_ERROR)
                            .passed(false)
                            .input(testCase.getInput())
                            .expectedOutput(testCase.getExpectedOutput())
                            .actualOutput("")
                            .errorMessage(e.getMessage())
                            .runtime(0)
                            .memory(0.0)
                            .build());
                    break;
                }
            }

            // Determine overall status
            ExecutionStatus overallStatus;
            if (passedCount == testCases.size()) {
                overallStatus = ExecutionStatus.ACCEPTED;
            } else {
                overallStatus = ExecutionStatus.WRONG_ANSWER;
            }

            return ExecutionResult.builder()
                    .status(overallStatus)
                    .testResults(testResults)
                    .totalTestCases(testCases.size())
                    .passedTestCases(passedCount)
                    .averageRuntime((int) (totalRuntime / testCases.size()))
                    .averageMemory(totalMemory / testCases.size())
                    .build();

        } catch (IOException e) {
            log.error("IO error during code execution", e);
            return ExecutionResult.builder()
                    .status(ExecutionStatus.RUNTIME_ERROR)
                    .errorMessage("Ichki xato: " + e.getMessage())
                    .testResults(new ArrayList<>())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during code execution", e);
            return ExecutionResult.builder()
                    .status(ExecutionStatus.RUNTIME_ERROR)
                    .errorMessage("Kutilmagan xato: " + e.getMessage())
                    .testResults(new ArrayList<>())
                    .build();
        } finally {
            // Cleanup
            if (workDir != null) {
                try {
                    dockerHelper.cleanupTempDir(workDir);
                    log.info("Cleaned up temp directory: {}", workDir);
                } catch (IOException e) {
                    log.warn("Failed to cleanup temp directory: {}", workDir, e);
                }
            }
        }
    }

    private TestCaseResult evaluateTestCase(TestCase testCase, DockerExecutionHelper.ExecutionResult runResult) {
        if (!runResult.isSuccess()) {
            ExecutionStatus status = runResult.getStdErr().contains("TIMEOUT")
                    ? ExecutionStatus.TIME_LIMIT_EXCEEDED
                    : ExecutionStatus.RUNTIME_ERROR;

            return TestCaseResult.builder()
                    .testCaseId(testCase.getId())
                    .status(status)
                    .passed(false)
                    .input(testCase.getInput())
                    .expectedOutput(testCase.getExpectedOutput())
                    .actualOutput(runResult.getStdOut())
                    .errorMessage(runResult.getStdErr())
                    .runtime((int) runResult.getTimeMs())
                    .memory(14.2) // Mock
                    .build();
        }

        // Compare output
        String actualOutput = runResult.getStdOut().trim();
        String expectedOutput = testCase.getExpectedOutput().trim();
        boolean passed = actualOutput.equals(expectedOutput);

        return TestCaseResult.builder()
                .testCaseId(testCase.getId())
                .status(passed ? ExecutionStatus.ACCEPTED : ExecutionStatus.WRONG_ANSWER)
                .passed(passed)
                .input(testCase.getInput())
                .expectedOutput(expectedOutput)
                .actualOutput(actualOutput)
                .runtime((int) runResult.getTimeMs())
                .memory(14.2) // Mock
                .build();
    }

    public enum ExecutionStatus {
        ACCEPTED,
        WRONG_ANSWER,
        TIME_LIMIT_EXCEEDED,
        MEMORY_LIMIT_EXCEEDED,
        RUNTIME_ERROR,
        COMPILE_ERROR
    }

    @lombok.Data
    @lombok.Builder
    public static class ExecutionResult {
        private ExecutionStatus status;
        private String errorMessage;
        private List<TestCaseResult> testResults;
        private int totalTestCases;
        private int passedTestCases;
        private int averageRuntime;
        private double averageMemory;
    }

    @lombok.Data
    @lombok.Builder
    public static class TestCaseResult {
        private Long testCaseId;
        private ExecutionStatus status;
        private boolean passed;
        private String input;
        private String expectedOutput;
        private String actualOutput;
        private String errorMessage;
        private int runtime;
        private double memory;
    }
}
