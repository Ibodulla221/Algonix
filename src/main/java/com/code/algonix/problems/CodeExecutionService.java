package com.code.algonix.problems;

import java.util.List;

/**
 * Kod bajarish servisi uchun interface
 */
public interface CodeExecutionService {

    /**
     * Kodni bajarish
     */
    ExecutionResult executeCode(String code, String language, List<TestCase> testCases);

    /**
     * Bajarish natijasi
     */
    enum ExecutionStatus {
        ACCEPTED,
        WRONG_ANSWER,
        TIME_LIMIT_EXCEEDED,
        MEMORY_LIMIT_EXCEEDED,
        RUNTIME_ERROR,
        COMPILE_ERROR
    }

    /**
     * Bajarish natijasi DTO
     */
    @lombok.Data
    @lombok.Builder
    class ExecutionResult {
        private ExecutionStatus status;
        private String errorMessage;
        private List<TestCaseResult> testResults;
        private int totalTestCases;
        private int passedTestCases;
        private int averageRuntime;
        private double averageMemory;
    }

    /**
     * Test case natijasi DTO
     */
    @lombok.Data
    @lombok.Builder
    class TestCaseResult {
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