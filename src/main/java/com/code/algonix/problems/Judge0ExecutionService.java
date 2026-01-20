package com.code.algonix.problems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class Judge0ExecutionService implements CodeExecutionService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${judge0.api.url:https://judge0-ce.p.rapidapi.com}")
    private String judge0ApiUrl;

    @Value("${judge0.api.key:}")
    private String rapidApiKey;

    // Language ID mapping for Judge0
    private static final Map<String, Integer> LANGUAGE_IDS = Map.ofEntries(
        Map.entry("javascript", 63),  // Node.js
        Map.entry("python", 71),      // Python 3
        Map.entry("java", 62),        // Java
        Map.entry("cpp", 54),         // C++
        Map.entry("c", 50),           // C
        Map.entry("csharp", 51),      // C#
        Map.entry("go", 60),          // Go
        Map.entry("rust", 73),        // Rust
        Map.entry("php", 68),         // PHP
        Map.entry("ruby", 72),        // Ruby
        Map.entry("swift", 83),       // Swift
        Map.entry("kotlin", 78),      // Kotlin
        Map.entry("scala", 81),       // Scala
        Map.entry("perl", 85),        // Perl
        Map.entry("r", 80),           // R
        Map.entry("dart", 90),        // Dart
        Map.entry("typescript", 74), // TypeScript
        Map.entry("bash", 46)         // Bash
    );

    public CodeExecutionService.ExecutionResult executeCode(String code, String language, List<TestCase> testCases) {
        try {
            Integer languageId = LANGUAGE_IDS.get(language.toLowerCase());
            if (languageId == null) {
                return createErrorResult("Unsupported language: " + language);
            }

            List<CodeExecutionService.TestCaseResult> testResults = new ArrayList<>();
            int passedCount = 0;

            for (TestCase testCase : testCases) {
                try {
                    // Submit code to Judge0
                    String token = submitCode(code, languageId, testCase.getInput());
                    if (token == null) {
                        testResults.add(createTestCaseError(testCase, "Failed to submit code"));
                        continue;
                    }

                    // Get result
                    Judge0Result result = getResult(token);
                    if (result == null) {
                        testResults.add(createTestCaseError(testCase, "Failed to get result"));
                        continue;
                    }

                    // Process result
                    CodeExecutionService.TestCaseResult testResult = processJudge0Result(testCase, result);
                    testResults.add(testResult);

                    if (testResult.isPassed()) {
                        passedCount++;
                    }

                    // Stop on first failure for hidden test cases
                    if (!testResult.isPassed() && testCase.getIsHidden()) {
                        break;
                    }

                } catch (Exception e) {
                    log.error("Error executing test case: {}", e.getMessage());
                    testResults.add(createTestCaseError(testCase, e.getMessage()));
                    break;
                }
            }

            // Determine overall status
            CodeExecutionService.ExecutionStatus overallStatus = (passedCount == testCases.size()) 
                ? CodeExecutionService.ExecutionStatus.ACCEPTED 
                : CodeExecutionService.ExecutionStatus.WRONG_ANSWER;

            return CodeExecutionService.ExecutionResult.builder()
                    .status(overallStatus)
                    .testResults(testResults)
                    .totalTestCases(testCases.size())
                    .passedTestCases(passedCount)
                    .averageRuntime(testResults.stream().mapToInt(CodeExecutionService.TestCaseResult::getRuntime).sum() / testResults.size())
                    .averageMemory(testResults.stream().mapToDouble(CodeExecutionService.TestCaseResult::getMemory).average().orElse(0.0))
                    .build();

        } catch (Exception e) {
            log.error("Error during Judge0 execution", e);
            return createErrorResult("Execution error: " + e.getMessage());
        }
    }

    private String submitCode(String code, int languageId, String input) {
        try {
            String url = judge0ApiUrl + "/submissions?base64_encoded=false&wait=true";

            Judge0SubmissionRequest request = new Judge0SubmissionRequest();
            request.setSourceCode(code);
            request.setLanguageId(languageId);
            request.setStdin(input);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (!rapidApiKey.isEmpty()) {
                headers.set("X-RapidAPI-Key", rapidApiKey);
                headers.set("X-RapidAPI-Host", "judge0-ce.p.rapidapi.com");
            }

            HttpEntity<Judge0SubmissionRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<Judge0SubmissionResponse> response = restTemplate.postForEntity(url, entity, Judge0SubmissionResponse.class);

            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                return response.getBody().getToken();
            }

            log.error("Failed to submit code: {}", response.getStatusCode());
            return null;

        } catch (Exception e) {
            log.error("Error submitting code to Judge0", e);
            return null;
        }
    }

    private Judge0Result getResult(String token) {
        try {
            String url = judge0ApiUrl + "/submissions/" + token + "?base64_encoded=false";

            HttpHeaders headers = new HttpHeaders();
            if (!rapidApiKey.isEmpty()) {
                headers.set("X-RapidAPI-Key", rapidApiKey);
                headers.set("X-RapidAPI-Host", "judge0-ce.p.rapidapi.com");
            }

            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Poll for result (Judge0 might need time to process)
            for (int i = 0; i < 10; i++) {
                ResponseEntity<Judge0Result> response = restTemplate.exchange(url, HttpMethod.GET, entity, Judge0Result.class);
                
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    Judge0Result result = response.getBody();
                    if (result.getStatus() != null && result.getStatus().getId() > 2) {
                        return result; // Processing complete
                    }
                }

                // Wait before next poll
                Thread.sleep(500);
            }

            log.error("Timeout waiting for Judge0 result");
            return null;

        } catch (Exception e) {
            log.error("Error getting result from Judge0", e);
            return null;
        }
    }

    private CodeExecutionService.TestCaseResult processJudge0Result(TestCase testCase, Judge0Result result) {
        String actualOutput = result.getStdout() != null ? result.getStdout().trim() : "";
        String expectedOutput = testCase.getExpectedOutput().trim();
        String errorMessage = result.getStderr();

        // Determine status based on Judge0 status
        CodeExecutionService.ExecutionStatus status;
        boolean passed = false;

        switch (result.getStatus().getId()) {
            case 3: // Accepted
                passed = actualOutput.equals(expectedOutput);
                status = passed ? CodeExecutionService.ExecutionStatus.ACCEPTED : CodeExecutionService.ExecutionStatus.WRONG_ANSWER;
                break;
            case 4: // Wrong Answer
                status = CodeExecutionService.ExecutionStatus.WRONG_ANSWER;
                break;
            case 5: // Time Limit Exceeded
                status = CodeExecutionService.ExecutionStatus.TIME_LIMIT_EXCEEDED;
                break;
            case 6: // Compilation Error
                status = CodeExecutionService.ExecutionStatus.COMPILE_ERROR;
                break;
            default: // Runtime Error or others
                status = CodeExecutionService.ExecutionStatus.RUNTIME_ERROR;
                break;
        }

        return CodeExecutionService.TestCaseResult.builder()
                .testCaseId(testCase.getId())
                .status(status)
                .passed(passed)
                .input(testCase.getInput())
                .expectedOutput(expectedOutput)
                .actualOutput(actualOutput)
                .errorMessage(errorMessage)
                .runtime(result.getTime() != null ? (int) (result.getTime() * 1000) : 0)
                .memory(result.getMemory() != null ? result.getMemory() / 1024.0 : 0.0)
                .build();
    }

    private CodeExecutionService.TestCaseResult createTestCaseError(TestCase testCase, String error) {
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

    private CodeExecutionService.ExecutionResult createErrorResult(String error) {
        return CodeExecutionService.ExecutionResult.builder()
                .status(CodeExecutionService.ExecutionStatus.RUNTIME_ERROR)
                .errorMessage(error)
                .testResults(new ArrayList<>())
                .totalTestCases(0)
                .passedTestCases(0)
                .averageRuntime(0)
                .averageMemory(0.0)
                .build();
    }

    // Judge0 API DTOs
    @Data
    public static class Judge0SubmissionRequest {
        @JsonProperty("source_code")
        private String sourceCode;
        
        @JsonProperty("language_id")
        private int languageId;
        
        private String stdin;
    }

    @Data
    public static class Judge0SubmissionResponse {
        private String token;
    }

    @Data
    public static class Judge0Result {
        private String stdout;
        private String stderr;
        private Double time;
        private Integer memory;
        private Judge0Status status;
    }

    @Data
    public static class Judge0Status {
        private int id;
        private String description;
    }
}