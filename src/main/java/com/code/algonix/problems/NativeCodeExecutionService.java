package com.code.algonix.problems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NativeCodeExecutionService implements CodeExecutionService {

    @Value("${code.execution.timeout-ms:5000}")
    private long timeoutMs;

    public CodeExecutionService.ExecutionResult executeCode(String code, String language, List<TestCase> testCases) {
        Path workDir = null;
        try {
            // Create temporary directory
            workDir = Files.createTempDirectory("algonix-native-");
            log.info("Created temp directory: {}", workDir);

            // Execute based on language
            switch (language.toLowerCase()) {
                case "javascript":
                    return executeJavaScript(code, testCases, workDir);
                case "python":
                    return executePython(code, testCases, workDir);
                case "java":
                    return executeJava(code, testCases, workDir);
                default:
                    throw new UnsupportedOperationException("Language not supported: " + language);
            }

        } catch (Exception e) {
            log.error("Error during native code execution", e);
            return CodeExecutionService.ExecutionResult.builder()
                    .status(CodeExecutionService.ExecutionStatus.RUNTIME_ERROR)
                    .errorMessage("Execution error: " + e.getMessage())
                    .testResults(new ArrayList<>())
                    .build();
        } finally {
            // Cleanup
            if (workDir != null) {
                cleanupDirectory(workDir);
            }
        }
    }

    private CodeExecutionService.ExecutionResult executeJavaScript(String code, List<TestCase> testCases, Path workDir) throws Exception {
        // Write JavaScript code to file
        Path jsFile = workDir.resolve("solution.js");
        Files.writeString(jsFile, code);

        List<CodeExecutionService.TestCaseResult> testResults = new ArrayList<>();
        int passedCount = 0;

        for (TestCase testCase : testCases) {
            try {
                // Run with Node.js
                ProcessBuilder pb = new ProcessBuilder("node", jsFile.toString());
                pb.directory(workDir.toFile());
                
                Process process = pb.start();
                
                // Send input
                try (PrintWriter writer = new PrintWriter(process.getOutputStream())) {
                    writer.println(testCase.getInput());
                    writer.flush();
                }

                // Wait for completion with timeout
                boolean finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
                
                if (!finished) {
                    process.destroyForcibly();
                    testResults.add(createTimeoutResult(testCase));
                    continue;
                }

                // Read output
                String output = readProcessOutput(process.getInputStream());
                String error = readProcessOutput(process.getErrorStream());

                if (process.exitValue() != 0) {
                    testResults.add(createErrorResult(testCase, error));
                    continue;
                }

                // Compare results
                boolean passed = output.trim().equals(testCase.getExpectedOutput().trim());
                testResults.add(createTestResult(testCase, output, passed));
                
                if (passed) passedCount++;

            } catch (Exception e) {
                testResults.add(createErrorResult(testCase, e.getMessage()));
            }
        }

        return createExecutionResult(testResults, passedCount, testCases.size());
    }

    private CodeExecutionService.ExecutionResult executePython(String code, List<TestCase> testCases, Path workDir) throws Exception {
        // Write Python code to file
        Path pyFile = workDir.resolve("solution.py");
        Files.writeString(pyFile, code);

        List<CodeExecutionService.TestCaseResult> testResults = new ArrayList<>();
        int passedCount = 0;

        for (TestCase testCase : testCases) {
            try {
                // Run with Python
                ProcessBuilder pb = new ProcessBuilder("python", pyFile.toString());
                pb.directory(workDir.toFile());
                
                Process process = pb.start();
                
                // Send input
                try (PrintWriter writer = new PrintWriter(process.getOutputStream())) {
                    writer.println(testCase.getInput());
                    writer.flush();
                }

                // Wait for completion with timeout
                boolean finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
                
                if (!finished) {
                    process.destroyForcibly();
                    testResults.add(createTimeoutResult(testCase));
                    continue;
                }

                // Read output
                String output = readProcessOutput(process.getInputStream());
                String error = readProcessOutput(process.getErrorStream());

                if (process.exitValue() != 0) {
                    testResults.add(createErrorResult(testCase, error));
                    continue;
                }

                // Compare results
                boolean passed = output.trim().equals(testCase.getExpectedOutput().trim());
                testResults.add(createTestResult(testCase, output, passed));
                
                if (passed) passedCount++;

            } catch (Exception e) {
                testResults.add(createErrorResult(testCase, e.getMessage()));
            }
        }

        return createExecutionResult(testResults, passedCount, testCases.size());
    }

    private CodeExecutionService.ExecutionResult executeJava(String code, List<TestCase> testCases, Path workDir) throws Exception {
        // Write Java code to file
        Path javaFile = workDir.resolve("Solution.java");
        Files.writeString(javaFile, code);

        // Compile Java code
        ProcessBuilder compileBuilder = new ProcessBuilder("javac", javaFile.toString());
        compileBuilder.directory(workDir.toFile());
        Process compileProcess = compileBuilder.start();
        
        boolean compileFinished = compileProcess.waitFor(10, TimeUnit.SECONDS);
        if (!compileFinished || compileProcess.exitValue() != 0) {
            String error = readProcessOutput(compileProcess.getErrorStream());
            return CodeExecutionService.ExecutionResult.builder()
                    .status(CodeExecutionService.ExecutionStatus.COMPILE_ERROR)
                    .errorMessage(error)
                    .testResults(new ArrayList<>())
                    .build();
        }

        List<CodeExecutionService.TestCaseResult> testResults = new ArrayList<>();
        int passedCount = 0;

        for (TestCase testCase : testCases) {
            try {
                // Run Java class
                ProcessBuilder pb = new ProcessBuilder("java", "-cp", workDir.toString(), "Solution");
                pb.directory(workDir.toFile());
                
                Process process = pb.start();
                
                // Send input
                try (PrintWriter writer = new PrintWriter(process.getOutputStream())) {
                    writer.println(testCase.getInput());
                    writer.flush();
                }

                // Wait for completion with timeout
                boolean finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
                
                if (!finished) {
                    process.destroyForcibly();
                    testResults.add(createTimeoutResult(testCase));
                    continue;
                }

                // Read output
                String output = readProcessOutput(process.getInputStream());
                String error = readProcessOutput(process.getErrorStream());

                if (process.exitValue() != 0) {
                    testResults.add(createErrorResult(testCase, error));
                    continue;
                }

                // Compare results
                boolean passed = output.trim().equals(testCase.getExpectedOutput().trim());
                testResults.add(createTestResult(testCase, output, passed));
                
                if (passed) passedCount++;

            } catch (Exception e) {
                testResults.add(createErrorResult(testCase, e.getMessage()));
            }
        }

        return createExecutionResult(testResults, passedCount, testCases.size());
    }

    private String readProcessOutput(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString().trim();
    }

    private CodeExecutionService.TestCaseResult createTestResult(TestCase testCase, String actualOutput, boolean passed) {
        return CodeExecutionService.TestCaseResult.builder()
                .testCaseId(testCase.getId())
                .status(passed ? CodeExecutionService.ExecutionStatus.ACCEPTED : CodeExecutionService.ExecutionStatus.WRONG_ANSWER)
                .passed(passed)
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .actualOutput(actualOutput)
                .runtime(100) // Mock runtime
                .memory(10.0) // Mock memory
                .build();
    }

    private CodeExecutionService.TestCaseResult createTimeoutResult(TestCase testCase) {
        return CodeExecutionService.TestCaseResult.builder()
                .testCaseId(testCase.getId())
                .status(CodeExecutionService.ExecutionStatus.TIME_LIMIT_EXCEEDED)
                .passed(false)
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .actualOutput("")
                .errorMessage("Time limit exceeded")
                .runtime((int) timeoutMs)
                .memory(0.0)
                .build();
    }

    private CodeExecutionService.TestCaseResult createErrorResult(TestCase testCase, String error) {
        return CodeExecutionService.TestCaseResult.builder()
                .testCaseId(testCase.getId())
                .status(CodeExecutionService.ExecutionStatus.RUNTIME_ERROR)
                .passed(false)
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .actualOutput("")
                .errorMessage(error)
                .runtime(0)
                .memory(0.0)
                .build();
    }

    private CodeExecutionService.ExecutionResult createExecutionResult(List<CodeExecutionService.TestCaseResult> testResults, int passedCount, int totalCount) {
        CodeExecutionService.ExecutionStatus status = (passedCount == totalCount) 
                ? CodeExecutionService.ExecutionStatus.ACCEPTED 
                : CodeExecutionService.ExecutionStatus.WRONG_ANSWER;

        return CodeExecutionService.ExecutionResult.builder()
                .status(status)
                .testResults(testResults)
                .totalTestCases(totalCount)
                .passedTestCases(passedCount)
                .averageRuntime(100) // Mock
                .averageMemory(10.0) // Mock
                .build();
    }

    private void cleanupDirectory(Path directory) {
        try {
            Files.walk(directory)
                    .sorted((a, b) -> b.compareTo(a)) // Delete files before directories
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            log.warn("Failed to delete: {}", path, e);
                        }
                    });
        } catch (IOException e) {
            log.warn("Failed to cleanup directory: {}", directory, e);
        }
    }
}