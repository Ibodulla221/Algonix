package com.code.algonix.problems;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * kep.uz kabi oddiy va samarali online judge tizimi
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleJudgeService implements CodeExecutionService {

    @Value("${judge.timeout-seconds:5}")
    private int timeoutSeconds;
    
    @Value("${judge.memory-limit-mb:64}")
    private int memoryLimitMB;
    
    @Value("${judge.max-output-size:10240}")
    private int maxOutputSize; // 10KB

    @Override
    public ExecutionResult executeCode(String code, String language, List<TestCase> testCases) {
        log.info("Starting simple judge execution for language: {}", language);
        
        // Kod validatsiya
        if (code == null || code.trim().isEmpty()) {
            return createErrorResult(ExecutionStatus.COMPILE_ERROR, "Kod bo'sh");
        }
        
        if (code.length() > 50000) { // 50KB limit
            return createErrorResult(ExecutionStatus.COMPILE_ERROR, "Kod juda uzun (maksimal 50KB)");
        }
        
        // Xavfli komandalarni tekshirish
        if (containsDangerousCode(code, language)) {
            return createErrorResult(ExecutionStatus.RUNTIME_ERROR, "Xavfli komandalar aniqlandi");
        }
        
        Path workDir = null;
        try {
            // Vaqtinchalik papka yaratish
            workDir = Files.createTempDirectory("judge-");
            log.debug("Created work directory: {}", workDir);
            
            // Tilga qarab bajarish
            return switch (language.toLowerCase()) {
                case "cpp", "c++" -> executeCpp(code, testCases, workDir);
                case "c" -> executeC(code, testCases, workDir);
                case "java" -> executeJava(code, testCases, workDir);
                case "python", "python3", "py" -> executePython(code, testCases, workDir);
                case "javascript", "js" -> executeJavaScript(code, testCases, workDir);
                case "csharp", "c#", "cs" -> executeCSharp(code, testCases, workDir);
                case "go", "golang" -> executeGo(code, testCases, workDir);
                case "rust", "rs" -> executeRust(code, testCases, workDir);
                case "php" -> executePhp(code, testCases, workDir);
                case "ruby", "rb" -> executeRuby(code, testCases, workDir);
                case "swift" -> executeSwift(code, testCases, workDir);
                case "kotlin", "kt" -> executeKotlin(code, testCases, workDir);
                case "scala" -> executeScala(code, testCases, workDir);
                case "perl", "pl" -> executePerl(code, testCases, workDir);
                case "r" -> executeR(code, testCases, workDir);
                case "dart" -> executeDart(code, testCases, workDir);
                case "typescript", "ts" -> executeTypeScript(code, testCases, workDir);
                case "bash", "sh" -> executeBash(code, testCases, workDir);
                default -> createErrorResult(ExecutionStatus.COMPILE_ERROR, "Qo'llab-quvvatlanmaydigan til: " + language);
            };
            
        } catch (Exception e) {
            log.error("Kod bajarish xatosi", e);
            return createErrorResult(ExecutionStatus.RUNTIME_ERROR, "Ichki xato: " + e.getMessage());
        } finally {
            // Tozalash
            if (workDir != null) {
                cleanupDirectory(workDir);
            }
        }
    }
    
    /**
     * C++ kod bajarish
     */
    private ExecutionResult executeCpp(String code, List<TestCase> testCases, Path workDir) throws Exception {
        // 1. Kod faylini yaratish
        Path sourceFile = workDir.resolve("solution.cpp");
        Files.writeString(sourceFile, code);
        
        // 2. Compile qilish
        Path executableFile = workDir.resolve("solution");
        ProcessBuilder compileBuilder = new ProcessBuilder(
            "g++", "-o", executableFile.toString(), 
            sourceFile.toString(), 
            "-std=c++17", "-O2"
        );
        compileBuilder.directory(workDir.toFile());
        compileBuilder.redirectErrorStream(true);
        
        Process compileProcess = compileBuilder.start();
        boolean compileFinished = compileProcess.waitFor(10, TimeUnit.SECONDS);
        
        if (!compileFinished || compileProcess.exitValue() != 0) {
            String compileError = readProcessOutput(compileProcess.getInputStream());
            return createErrorResult(ExecutionStatus.COMPILE_ERROR, "Compile xatosi:\n" + compileError);
        }
        
        log.debug("C++ compilation successful");
        
        // 3. Test case'lar bilan tekshirish
        return runTestCases(new String[]{executableFile.toString()}, testCases, workDir);
    }
    
    /**
     * Java kod bajarish
     */
    private ExecutionResult executeJava(String code, List<TestCase> testCases, Path workDir) throws Exception {
        // Class nomini topish
        String className = extractJavaClassName(code);
        if (className == null) {
            return createErrorResult(ExecutionStatus.COMPILE_ERROR, "Java class nomi topilmadi");
        }
        
        Path sourceFile = workDir.resolve(className + ".java");
        Files.writeString(sourceFile, code);
        
        // Compile
        ProcessBuilder compileBuilder = new ProcessBuilder("javac", sourceFile.toString());
        compileBuilder.directory(workDir.toFile());
        compileBuilder.redirectErrorStream(true);
        
        Process compileProcess = compileBuilder.start();
        boolean compileFinished = compileProcess.waitFor(15, TimeUnit.SECONDS);
        
        if (!compileFinished || compileProcess.exitValue() != 0) {
            String compileError = readProcessOutput(compileProcess.getInputStream());
            return createErrorResult(ExecutionStatus.COMPILE_ERROR, "Compile xatosi:\n" + compileError);
        }
        
        // Run
        String[] command = {"java", "-cp", workDir.toString(), className};
        return runTestCases(command, testCases, workDir);
    }
    
    /**
     * Python kod bajarish
     */
    private ExecutionResult executePython(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path sourceFile = workDir.resolve("solution.py");
        Files.writeString(sourceFile, code);
        
        String[] command = {"python", sourceFile.toString()};
        return runTestCases(command, testCases, workDir);
    }
    
    /**
     * JavaScript kod bajarish
     */
    private ExecutionResult executeJavaScript(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path sourceFile = workDir.resolve("solution.js");
        Files.writeString(sourceFile, code);
        
        String[] command = {"node", sourceFile.toString()};
        return runTestCases(command, testCases, workDir);
    }
    
    /**
     * C kod bajarish
     */
    private ExecutionResult executeC(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path sourceFile = workDir.resolve("solution.c");
        Files.writeString(sourceFile, code);
        
        Path executableFile = workDir.resolve("solution");
        ProcessBuilder compileBuilder = new ProcessBuilder(
            "gcc", "-o", executableFile.toString(), 
            sourceFile.toString(), 
            "-std=c11", "-O2"
        );
        compileBuilder.directory(workDir.toFile());
        compileBuilder.redirectErrorStream(true);
        
        Process compileProcess = compileBuilder.start();
        boolean compileFinished = compileProcess.waitFor(10, TimeUnit.SECONDS);
        
        if (!compileFinished || compileProcess.exitValue() != 0) {
            String compileError = readProcessOutput(compileProcess.getInputStream());
            return createErrorResult(ExecutionStatus.COMPILE_ERROR, "Compile xatosi:\n" + compileError);
        }
        
        return runTestCases(new String[]{executableFile.toString()}, testCases, workDir);
    }
    
    /**
     * Test case'larni bajarish
     */
    private ExecutionResult runTestCases(String[] command, List<TestCase> testCases, Path workDir) throws Exception {
        List<TestCaseResult> results = new ArrayList<>();
        int passedCount = 0;
        
        for (int i = 0; i < testCases.size(); i++) {
            TestCase testCase = testCases.get(i);
            log.debug("Running test case {}: input={}", i + 1, testCase.getInput());
            
            try {
                // Process yaratish
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.directory(workDir.toFile());
                pb.redirectErrorStream(false);
                
                Process process = pb.start();
                
                // Input berish
                try (PrintWriter writer = new PrintWriter(process.getOutputStream())) {
                    writer.print(testCase.getInput());
                    writer.flush();
                }
                
                // Timeout bilan kutish
                boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
                
                if (!finished) {
                    process.destroyForcibly();
                    TestCaseResult result = TestCaseResult.builder()
                        .testCaseId(testCase.getId())
                        .status(ExecutionStatus.TIME_LIMIT_EXCEEDED)
                        .passed(false)
                        .input(testCase.getInput())
                        .expectedOutput(testCase.getExpectedOutput())
                        .actualOutput("")
                        .errorMessage("Vaqt tugadi (" + timeoutSeconds + "s)")
                        .build();
                    results.add(result);
                    continue;
                }
                
                // Exit code tekshirish
                if (process.exitValue() != 0) {
                    String errorOutput = readProcessOutput(process.getErrorStream());
                    TestCaseResult result = TestCaseResult.builder()
                        .testCaseId(testCase.getId())
                        .status(ExecutionStatus.RUNTIME_ERROR)
                        .passed(false)
                        .input(testCase.getInput())
                        .expectedOutput(testCase.getExpectedOutput())
                        .actualOutput("")
                        .errorMessage("Runtime xato: " + errorOutput)
                        .build();
                    results.add(result);
                    continue;
                }
                
                // Output olish
                String actualOutput = readProcessOutput(process.getInputStream());
                
                // Output hajmini tekshirish
                if (actualOutput.length() > maxOutputSize) {
                    TestCaseResult result = TestCaseResult.builder()
                        .testCaseId(testCase.getId())
                        .status(ExecutionStatus.RUNTIME_ERROR)
                        .passed(false)
                        .input(testCase.getInput())
                        .expectedOutput(testCase.getExpectedOutput())
                        .actualOutput(actualOutput.substring(0, 100) + "...")
                        .errorMessage("Output juda katta")
                        .build();
                    results.add(result);
                    continue;
                }
                
                // Javobni taqqoslash
                String expected = testCase.getExpectedOutput().trim();
                String actual = actualOutput.trim();
                
                if (compareOutputs(expected, actual)) {
                    passedCount++;
                    TestCaseResult result = TestCaseResult.builder()
                        .testCaseId(testCase.getId())
                        .status(ExecutionStatus.ACCEPTED)
                        .passed(true)
                        .input(testCase.getInput())
                        .expectedOutput(expected)
                        .actualOutput(actual)
                        .errorMessage(null)
                        .build();
                    results.add(result);
                } else {
                    TestCaseResult result = TestCaseResult.builder()
                        .testCaseId(testCase.getId())
                        .status(ExecutionStatus.WRONG_ANSWER)
                        .passed(false)
                        .input(testCase.getInput())
                        .expectedOutput(expected)
                        .actualOutput(actual)
                        .errorMessage("Kutilgan: " + expected + ", Olingan: " + actual)
                        .build();
                    results.add(result);
                }
                
            } catch (Exception e) {
                log.error("Test case {} bajarishda xato", i + 1, e);
                TestCaseResult result = TestCaseResult.builder()
                    .testCaseId(testCase.getId())
                    .status(ExecutionStatus.RUNTIME_ERROR)
                    .passed(false)
                    .input(testCase.getInput())
                    .expectedOutput(testCase.getExpectedOutput())
                    .actualOutput("")
                    .errorMessage("Ichki xato: " + e.getMessage())
                    .build();
                results.add(result);
            }
        }
        
        // Natijani qaytarish
        ExecutionStatus finalStatus = (passedCount == testCases.size()) ? 
            ExecutionStatus.ACCEPTED : ExecutionStatus.WRONG_ANSWER;
            
        String message = (passedCount == testCases.size()) ? 
            "Barcha test case'lar o'tdi!" : 
            passedCount + "/" + testCases.size() + " test case o'tdi";
        
        return ExecutionResult.builder()
            .status(finalStatus)
            .errorMessage(message)
            .testResults(results)
            .totalTestCases(testCases.size())
            .passedTestCases(passedCount)
            .averageRuntime(0)
            .averageMemory(0.0)
            .build();
    }
    
    /**
     * Output'larni taqqoslash
     */
    private boolean compareOutputs(String expected, String actual) {
        expected = normalizeOutput(expected);
        actual = normalizeOutput(actual);
        return expected.equals(actual);
    }
    
    private String normalizeOutput(String output) {
        if (output == null) return "";
        
        // Trailing whitespace'larni olib tashlash
        output = output.trim();
        
        // Multiple spaces'ni bitta space bilan almashtirish
        output = output.replaceAll("\\s+", " ");
        
        // Line ending'larni normalize qilish
        output = output.replaceAll("\\r\\n", "\n");
        output = output.replaceAll("\\r", "\n");
        
        return output;
    }
    
    /**
     * C# kod bajarish
     */
    private ExecutionResult executeCSharp(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path sourceFile = workDir.resolve("solution.cs");
        Files.writeString(sourceFile, code);
        
        Path executableFile = workDir.resolve("solution.exe");
        ProcessBuilder compileBuilder = new ProcessBuilder(
            "csc", "/out:" + executableFile.toString(), 
            sourceFile.toString()
        );
        compileBuilder.directory(workDir.toFile());
        compileBuilder.redirectErrorStream(true);
        
        Process compileProcess = compileBuilder.start();
        boolean compileFinished = compileProcess.waitFor(10, TimeUnit.SECONDS);
        
        if (!compileFinished || compileProcess.exitValue() != 0) {
            String compileError = readProcessOutput(compileProcess.getInputStream());
            return createErrorResult(ExecutionStatus.COMPILE_ERROR, "Compile xatosi:\n" + compileError);
        }
        
        return runTestCases(new String[]{executableFile.toString()}, testCases, workDir);
    }
    
    /**
     * Go kod bajarish
     */
    private ExecutionResult executeGo(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path sourceFile = workDir.resolve("solution.go");
        Files.writeString(sourceFile, code);
        
        Path executableFile = workDir.resolve("solution");
        ProcessBuilder compileBuilder = new ProcessBuilder(
            "go", "build", "-o", executableFile.toString(), 
            sourceFile.toString()
        );
        compileBuilder.directory(workDir.toFile());
        compileBuilder.redirectErrorStream(true);
        
        Process compileProcess = compileBuilder.start();
        boolean compileFinished = compileProcess.waitFor(10, TimeUnit.SECONDS);
        
        if (!compileFinished || compileProcess.exitValue() != 0) {
            String compileError = readProcessOutput(compileProcess.getInputStream());
            return createErrorResult(ExecutionStatus.COMPILE_ERROR, "Compile xatosi:\n" + compileError);
        }
        
        return runTestCases(new String[]{executableFile.toString()}, testCases, workDir);
    }
    
    /**
     * Rust kod bajarish
     */
    private ExecutionResult executeRust(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path sourceFile = workDir.resolve("solution.rs");
        Files.writeString(sourceFile, code);
        
        Path executableFile = workDir.resolve("solution");
        ProcessBuilder compileBuilder = new ProcessBuilder(
            "rustc", "-o", executableFile.toString(), 
            sourceFile.toString()
        );
        compileBuilder.directory(workDir.toFile());
        compileBuilder.redirectErrorStream(true);
        
        Process compileProcess = compileBuilder.start();
        boolean compileFinished = compileProcess.waitFor(10, TimeUnit.SECONDS);
        
        if (!compileFinished || compileProcess.exitValue() != 0) {
            String compileError = readProcessOutput(compileProcess.getInputStream());
            return createErrorResult(ExecutionStatus.COMPILE_ERROR, "Compile xatosi:\n" + compileError);
        }
        
        return runTestCases(new String[]{executableFile.toString()}, testCases, workDir);
    }
    
    /**
     * PHP kod bajarish
     */
    private ExecutionResult executePhp(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path sourceFile = workDir.resolve("solution.php");
        Files.writeString(sourceFile, code);
        
        String[] command = {"php", sourceFile.toString()};
        return runTestCases(command, testCases, workDir);
    }
    
    /**
     * Ruby kod bajarish
     */
    private ExecutionResult executeRuby(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path sourceFile = workDir.resolve("solution.rb");
        Files.writeString(sourceFile, code);
        
        String[] command = {"ruby", sourceFile.toString()};
        return runTestCases(command, testCases, workDir);
    }
    
    /**
     * Swift kod bajarish
     */
    private ExecutionResult executeSwift(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path sourceFile = workDir.resolve("solution.swift");
        Files.writeString(sourceFile, code);
        
        Path executableFile = workDir.resolve("solution");
        ProcessBuilder compileBuilder = new ProcessBuilder(
            "swiftc", "-o", executableFile.toString(), 
            sourceFile.toString()
        );
        compileBuilder.directory(workDir.toFile());
        compileBuilder.redirectErrorStream(true);
        
        Process compileProcess = compileBuilder.start();
        boolean compileFinished = compileProcess.waitFor(10, TimeUnit.SECONDS);
        
        if (!compileFinished || compileProcess.exitValue() != 0) {
            String compileError = readProcessOutput(compileProcess.getInputStream());
            return createErrorResult(ExecutionStatus.COMPILE_ERROR, "Compile xatosi:\n" + compileError);
        }
        
        return runTestCases(new String[]{executableFile.toString()}, testCases, workDir);
    }
    
    /**
     * Kotlin kod bajarish
     */
    private ExecutionResult executeKotlin(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path sourceFile = workDir.resolve("solution.kt");
        Files.writeString(sourceFile, code);
        
        Path jarFile = workDir.resolve("solution.jar");
        ProcessBuilder compileBuilder = new ProcessBuilder(
            "kotlinc", sourceFile.toString(), "-include-runtime", "-d", jarFile.toString()
        );
        compileBuilder.directory(workDir.toFile());
        compileBuilder.redirectErrorStream(true);
        
        Process compileProcess = compileBuilder.start();
        boolean compileFinished = compileProcess.waitFor(15, TimeUnit.SECONDS);
        
        if (!compileFinished || compileProcess.exitValue() != 0) {
            String compileError = readProcessOutput(compileProcess.getInputStream());
            return createErrorResult(ExecutionStatus.COMPILE_ERROR, "Compile xatosi:\n" + compileError);
        }
        
        return runTestCases(new String[]{"java", "-jar", jarFile.toString()}, testCases, workDir);
    }
    
    /**
     * Scala kod bajarish
     */
    private ExecutionResult executeScala(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path sourceFile = workDir.resolve("solution.scala");
        Files.writeString(sourceFile, code);
        
        String[] command = {"scala", sourceFile.toString()};
        return runTestCases(command, testCases, workDir);
    }
    
    /**
     * Perl kod bajarish
     */
    private ExecutionResult executePerl(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path sourceFile = workDir.resolve("solution.pl");
        Files.writeString(sourceFile, code);
        
        String[] command = {"perl", sourceFile.toString()};
        return runTestCases(command, testCases, workDir);
    }
    
    /**
     * R kod bajarish
     */
    private ExecutionResult executeR(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path sourceFile = workDir.resolve("solution.R");
        Files.writeString(sourceFile, code);
        
        String[] command = {"Rscript", sourceFile.toString()};
        return runTestCases(command, testCases, workDir);
    }
    
    /**
     * Dart kod bajarish
     */
    private ExecutionResult executeDart(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path sourceFile = workDir.resolve("solution.dart");
        Files.writeString(sourceFile, code);
        
        String[] command = {"dart", sourceFile.toString()};
        return runTestCases(command, testCases, workDir);
    }
    
    /**
     * TypeScript kod bajarish
     */
    private ExecutionResult executeTypeScript(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path sourceFile = workDir.resolve("solution.ts");
        Files.writeString(sourceFile, code);
        
        // TypeScript ni JavaScript ga compile qilish
        Path jsFile = workDir.resolve("solution.js");
        ProcessBuilder compileBuilder = new ProcessBuilder(
            "tsc", sourceFile.toString(), "--outFile", jsFile.toString()
        );
        compileBuilder.directory(workDir.toFile());
        compileBuilder.redirectErrorStream(true);
        
        Process compileProcess = compileBuilder.start();
        boolean compileFinished = compileProcess.waitFor(10, TimeUnit.SECONDS);
        
        if (!compileFinished || compileProcess.exitValue() != 0) {
            String compileError = readProcessOutput(compileProcess.getInputStream());
            return createErrorResult(ExecutionStatus.COMPILE_ERROR, "TypeScript compile xatosi:\n" + compileError);
        }
        
        return runTestCases(new String[]{"node", jsFile.toString()}, testCases, workDir);
    }
    
    /**
     * Bash kod bajarish
     */
    private ExecutionResult executeBash(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path sourceFile = workDir.resolve("solution.sh");
        Files.writeString(sourceFile, code);
        
        // Bash faylini executable qilish
        sourceFile.toFile().setExecutable(true);
        
        String[] command = {"bash", sourceFile.toString()};
        return runTestCases(command, testCases, workDir);
    }
    
    /**
     * Xavfli kod tekshirish
     */
    private boolean containsDangerousCode(String code, String language) {
        String[] dangerousPatterns = {
            "system(", "exec(", "popen(", "fork(", "kill(",
            "remove(", "unlink(", "rmdir(", "mkdir(",
            "#include <windows.h>", "#include <unistd.h>",
            "Runtime.getRuntime()", "ProcessBuilder",
            "import os", "import subprocess", "import sys",
            "require('fs')", "require('child_process')",
            "eval(", "Function(", "__import__"
        };
        
        String lowerCode = code.toLowerCase();
        for (String pattern : dangerousPatterns) {
            if (lowerCode.contains(pattern.toLowerCase())) {
                log.warn("Dangerous code detected: {}", pattern);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Java class nomini topish
     */
    private String extractJavaClassName(String code) {
        String[] lines = code.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("public class ")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 3) {
                    return parts[2].replace("{", "").trim();
                }
            }
        }
        return null;
    }
    
    /**
     * Process output'ini o'qish
     */
    private String readProcessOutput(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                
                // Output hajmini cheklash
                if (output.length() > maxOutputSize) {
                    break;
                }
            }
        }
        return output.toString();
    }
    
    /**
     * Xato natijasini yaratish
     */
    private ExecutionResult createErrorResult(ExecutionStatus status, String message) {
        return ExecutionResult.builder()
            .status(status)
            .errorMessage(message)
            .testResults(new ArrayList<>())
            .totalTestCases(0)
            .passedTestCases(0)
            .averageRuntime(0)
            .averageMemory(0.0)
            .build();
    }
    
    /**
     * Papkani tozalash
     */
    private void cleanupDirectory(Path directory) {
        try {
            Files.walk(directory)
                .sorted((a, b) -> b.compareTo(a)) // Fayllarni birinchi o'chirish
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