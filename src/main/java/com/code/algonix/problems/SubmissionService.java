package com.code.algonix.problems;

import com.code.algonix.problems.Problem;
import com.code.algonix.problems.Submission;
import com.code.algonix.problems.TestCase;
import com.code.algonix.problems.ProblemRepository;
import com.code.algonix.problems.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;
    private final DockerExecutionHelper dockerHelper; // yordamchi: bajarishning ayrim kodlari

    public Map<String, Object> submit(Long userId, Long problemId, String language, String code) throws Exception {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        Submission submission = Submission.builder()
                .userId(userId)
                .problemId(problemId)
                .language(language)
                .code(code)
                .status("PENDING")
                .createdAt(Instant.now())
                .build();
        submission = submissionRepository.save(submission);

        submission.setStatus("RUNNING");
        submissionRepository.save(submission);

        // temp dir
        Path tempDir = Files.createTempDirectory("sub-" + submission.getId() + "-");
        try {
            // create source file
            String fileName = dockerHelper.sourceFileNameFor(language);
            Path sourcePath = tempDir.resolve(fileName);
            Files.writeString(sourcePath, code, StandardOpenOption.CREATE);

            // for languages needing additional files (Java: add Main wrapper if not present) user must provide correct class name
            List<Map<String,Object>> perTestResults = new ArrayList<>();
            boolean allPassed = true;

            for (TestCase tc : problem.getTestCases()) {
                // compile step (if needed)
                DockerExecutionHelper.ExecutionResult compileResult = dockerHelper.compileIfNeeded(language, tempDir);
                if (!compileResult.isSuccess()) {
                    Map<String,Object> r = Map.of(
                            "testId", tc.getId(),
                            "status", "COMPILE_ERROR",
                            "message", compileResult.getStdErr()
                    );
                    perTestResults.add(r);
                    allPassed = false;
                    break; // stop on compile error
                }

                // run with input
                DockerExecutionHelper.ExecutionResult runResult = dockerHelper.runExecutable(language, tempDir, tc.getInputData(), tc.getTimeLimitMs());

                String stdout = runResult.getStdOut();
                String stderr = runResult.getStdErr();
                boolean passed = compareOutput(stdout, tc.getExpectedOutput());

                Map<String,Object> r = new HashMap<>();
                r.put("testId", tc.getId());
                r.put("passed", passed);
                r.put("stdout", stdout);
                r.put("stderr", stderr);
                r.put("timeMs", runResult.getTimeMs());
                perTestResults.add(r);

                if (!passed) allPassed = false;

                // If you want stop-on-first-fail: uncomment:
                // if (!passed) break;
            }

            submission.setStatus(allPassed ? "PASSED" : "FAILED");
            submission.setResultJson(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(perTestResults));
            submissionRepository.save(submission);

            Map<String,Object> resp = new HashMap<>();
            resp.put("submissionId", submission.getId());
            resp.put("status", submission.getStatus());
            resp.put("results", perTestResults);
            return resp;
        } finally {
            // cleanup temp dir
            try {
                dockerHelper.cleanupTempDir(tempDir);
            } catch (Exception ignored) {}
        }
    }

    private boolean compareOutput(String stdout, String expected) {
        // Simple exact match ignoring trailing spaces and newlines
        if (stdout == null) stdout = "";
        if (expected == null) expected = "";
        String a = stdout.strip();
        String b = expected.strip();
        return a.equals(b);
    }
}
