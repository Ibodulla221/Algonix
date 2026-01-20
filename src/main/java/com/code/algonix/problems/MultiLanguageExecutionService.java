package com.code.algonix.problems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MultiLanguageExecutionService implements CodeExecutionService {

    private final SecurityExecutionService securityService;
    private final ResourceMonitoringService resourceMonitoringService;

    @Value("${code.execution.timeout-ms:10000}")
    private long timeoutMs;
    
    @Value("${code.execution.memory-limit:128}")
    private int memoryLimitMB;
    
    @Value("${code.execution.max-output-size:1048576}") // 1MB
    private int maxOutputSize;
    
    @Value("${code.execution.max-file-size:65536}") // 64KB
    private int maxFileSize;

    @Override
    public ExecutionResult executeCode(String code, String language, List<TestCase> testCases) {
        // Tizim resurslarini tekshirish
        if (!resourceMonitoringService.hasEnoughResources()) {
            return createErrorResult("Tizim resurslari yetarli emas");
        }
        
        // Kod hajmini tekshirish
        if (code.length() > maxFileSize) {
            return createErrorResult("Kod hajmi juda katta (maksimal: " + maxFileSize + " bayt)");
        }
        
        // Xavfli komandalarni tekshirish
        if (!securityService.isCodeSafe(code, language)) {
            return createErrorResult("Xavfli komandalar aniqlandi");
        }
        
        Path workDir = null;
        try {
            workDir = Files.createTempDirectory("algonix-simple-");
            securityService.secureWorkDirectory(workDir);
            log.info("Created secure temp directory: {}", workDir);

            return switch (language.toLowerCase()) {
                case "javascript", "js" -> executeJavaScript(code, testCases, workDir);
                case "python", "python3", "py" -> executePython(code, testCases, workDir);
                case "java" -> executeJava(code, testCases, workDir);
                case "cpp", "c++" -> {
                    // C++ compiler o'rnatilganligini tekshirish
                    try {
                        ProcessBuilder gppCheck = new ProcessBuilder("g++", "--version");
                        Process gppProcess = gppCheck.start();
                        if (gppProcess.waitFor(5, TimeUnit.SECONDS) && gppProcess.exitValue() == 0) {
                            yield executeCpp(code, testCases, workDir);
                        } else {
                            yield createErrorResult("C++ compiler (g++) o'rnatilmagan. Iltimos, MinGW-w64 yoki MSYS2 o'rnating.");
                        }
                    } catch (Exception e) {
                        yield createErrorResult("C++ compiler o'rnatilmagan. Iltimos, https://www.msys2.org/ dan MSYS2 o'rnating.");
                    }
                }
                case "php" -> executePhp(code, testCases, workDir);
                default -> createErrorResult("Qo'llab-quvvatlanmaydigan til: " + language + ". Hozircha JavaScript, Python, Java qo'llab-quvvatlanadi.");
            };

        } catch (IOException e) {
            log.error("IO xatosi kod bajarish vaqtida", e);
            return createErrorResult("Fayl tizimi xatosi: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Jarayon to'xtatildi", e);
            return createErrorResult("Bajarish to'xtatildi");
        } catch (Exception e) {
            log.error("Kutilmagan xato kod bajarish vaqtida", e);
            return createErrorResult("Bajarish xatosi: " + e.getMessage());
        } finally {
            if (workDir != null) {
                securityService.cleanupSecurely(workDir);
            }
        }
    }

    // JavaScript (Node.js)
    private ExecutionResult executeJavaScript(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path jsFile = workDir.resolve("solution.js");
        
        // Code template system - agar faqat console.log bo'lsa, input reading qo'shamiz
        String processedCode = processJavaScriptCode(code);
        
        Files.writeString(jsFile, processedCode);
        log.debug("JavaScript file created: {}", jsFile);
        log.debug("Original JavaScript code: {}", code);
        log.debug("Processed JavaScript code: {}", processedCode);
        return executeWithCommand(new String[]{"node", jsFile.toString()}, testCases, workDir);
    }

    // Python
    private ExecutionResult executePython(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path pyFile = workDir.resolve("solution.py");
        Files.writeString(pyFile, code);
        return executeWithCommand(new String[]{"python", pyFile.toString()}, testCases, workDir);
    }

    // Java
    private ExecutionResult executeJava(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path javaFile = workDir.resolve("Solution.java");
        Files.writeString(javaFile, code);

        // Compile
        ProcessBuilder compileBuilder = new ProcessBuilder("javac", javaFile.toString());
        compileBuilder.directory(workDir.toFile());
        Process compileProcess = compileBuilder.start();
        
        if (!compileProcess.waitFor(10, TimeUnit.SECONDS) || compileProcess.exitValue() != 0) {
            String error = readProcessOutput(compileProcess.getErrorStream());
            return createCompileErrorResult(error);
        }

        return executeWithCommand(new String[]{"java", "-cp", workDir.toString(), "Solution"}, testCases, workDir);
    }

    // C++
    private ExecutionResult executeCpp(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path cppFile = workDir.resolve("solution.cpp");
        Path exeFile = workDir.resolve("solution.exe");
        Files.writeString(cppFile, code);

        // Compile
        ProcessBuilder compileBuilder = new ProcessBuilder("g++", "-o", exeFile.toString(), cppFile.toString());
        compileBuilder.directory(workDir.toFile());
        Process compileProcess = compileBuilder.start();
        
        if (!compileProcess.waitFor(10, TimeUnit.SECONDS) || compileProcess.exitValue() != 0) {
            String error = readProcessOutput(compileProcess.getErrorStream());
            return createCompileErrorResult(error);
        }

        return executeWithCommand(new String[]{exeFile.toString()}, testCases, workDir);
    }

    // PHP
    private ExecutionResult executePhp(String code, List<TestCase> testCases, Path workDir) throws Exception {
        Path phpFile = workDir.resolve("solution.php");
        String phpCode = "<?php\n" + code;
        Files.writeString(phpFile, phpCode);
        
        log.debug("PHP file created: {}", phpFile);
        log.debug("PHP code: {}", phpCode);
        
        // Absolute path to PHP
        String phpPath = "C:\\php\\php.exe";
        return executeWithCommand(new String[]{phpPath, phpFile.toString()}, testCases, workDir);
    }

    // Generic execution method
    private ExecutionResult executeWithCommand(String[] command, List<TestCase> testCases, Path workDir) {
        List<TestCaseResult> testResults = new ArrayList<>();
        int passedCount = 0;

        for (TestCase testCase : testCases) {
            try {
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.directory(workDir.toFile());
                
                // Xavfsizlik: Environment variables'ni cheklash
                pb.environment().clear();
                pb.environment().put("PATH", System.getenv("PATH"));
                pb.environment().put("HOME", workDir.toString());
                pb.environment().put("TMPDIR", workDir.toString());
                
                Process process = pb.start();
                
                // Send input with size limit
                try (PrintWriter writer = new PrintWriter(process.getOutputStream())) {
                    String input = testCase.getInput();
                    if (input.length() > maxOutputSize) {
                        input = input.substring(0, maxOutputSize);
                    }
                    writer.println(input);
                    writer.flush();
                }

                // Wait for completion with timeout
                boolean finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
                
                if (!finished) {
                    process.destroyForcibly();
                    if (!process.waitFor(2, TimeUnit.SECONDS)) {
                        log.warn("Process did not terminate gracefully");
                    }
                    testResults.add(createTimeoutResult(testCase));
                    continue;
                }

                // Read output with size limit
                String output = readProcessOutputWithLimit(process.getInputStream());
                String error = readProcessOutputWithLimit(process.getErrorStream());
                
                log.debug("Process exit code: {}", process.exitValue());
                log.debug("Process output: '{}'", output);
                log.debug("Process error: '{}'", error);

                // JavaScript uchun exit code'ni ignore qilamiz agar output bor bo'lsa
                if (process.exitValue() != 0 && output.trim().isEmpty()) {
                    testResults.add(createErrorResult(testCase, error));
                    continue;
                }

                // Compare results
                boolean passed = output.trim().equals(testCase.getExpectedOutput().trim());
                testResults.add(createTestResult(testCase, output, passed));
                
                if (passed) passedCount++;

                // Stop on first failure for hidden test cases
                if (!passed && testCase.getIsHidden()) {
                    break;
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                testResults.add(createErrorResult(testCase, "Jarayon to'xtatildi"));
                break;
            } catch (Exception e) {
                testResults.add(createErrorResult(testCase, e.getMessage()));
                break;
            }
        }

        return createExecutionResult(testResults, passedCount, testCases.size());
    }

    // Helper methods
    private String readProcessOutputWithLimit(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int totalSize = 0;
            while ((line = reader.readLine()) != null) {
                totalSize += line.length() + 1; // +1 for newline
                if (totalSize > maxOutputSize) {
                    output.append("... [Chiqish hajmi juda katta, qisqartirildi]");
                    break;
                }
                output.append(line).append("\n");
            }
        }
        return output.toString().trim();
    }

    private String readProcessOutput(InputStream inputStream) throws IOException {
        return readProcessOutputWithLimit(inputStream);
    }

    private TestCaseResult createTestResult(TestCase testCase, String actualOutput, boolean passed) {
        return TestCaseResult.builder()
                .testCaseId(testCase.getId())
                .status(passed ? ExecutionStatus.ACCEPTED : ExecutionStatus.WRONG_ANSWER)
                .passed(passed)
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .actualOutput(actualOutput)
                .runtime(100) // Mock runtime
                .memory(10.0) // Mock memory
                .build();
    }

    private TestCaseResult createTimeoutResult(TestCase testCase) {
        return TestCaseResult.builder()
                .testCaseId(testCase.getId())
                .status(ExecutionStatus.TIME_LIMIT_EXCEEDED)
                .passed(false)
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .actualOutput("")
                .errorMessage("Vaqt tugadi")
                .runtime((int) timeoutMs)
                .memory(0.0)
                .build();
    }

    private TestCaseResult createErrorResult(TestCase testCase, String error) {
        return TestCaseResult.builder()
                .testCaseId(testCase.getId())
                .status(ExecutionStatus.RUNTIME_ERROR)
                .passed(false)
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .actualOutput("")
                .errorMessage(error)
                .runtime(0)
                .memory(0.0)
                .build();
    }

    private ExecutionResult createCompileErrorResult(String error) {
        return ExecutionResult.builder()
                .status(ExecutionStatus.COMPILE_ERROR)
                .errorMessage("Kompilyatsiya xatosi: " + error)
                .testResults(new ArrayList<>())
                .totalTestCases(0)
                .passedTestCases(0)
                .averageRuntime(0)
                .averageMemory(0.0)
                .build();
    }

    private ExecutionResult createErrorResult(String error) {
        return ExecutionResult.builder()
                .status(ExecutionStatus.RUNTIME_ERROR)
                .errorMessage(error)
                .testResults(new ArrayList<>())
                .totalTestCases(0)
                .passedTestCases(0)
                .averageRuntime(0)
                .averageMemory(0.0)
                .build();
    }

    private ExecutionResult createExecutionResult(List<TestCaseResult> testResults, int passedCount, int totalCount) {
        ExecutionStatus status = (passedCount == totalCount) 
                ? ExecutionStatus.ACCEPTED 
                : ExecutionStatus.WRONG_ANSWER;

        return ExecutionResult.builder()
                .status(status)
                .testResults(testResults)
                .totalTestCases(totalCount)
                .passedTestCases(passedCount)
                .averageRuntime(testResults.isEmpty() ? 0 : testResults.stream().mapToInt(TestCaseResult::getRuntime).sum() / testResults.size())
                .averageMemory(testResults.isEmpty() ? 0.0 : testResults.stream().mapToDouble(TestCaseResult::getMemory).average().orElse(0.0))
                .build();
    }
    
    /**
     * JavaScript kodini qayta ishlash - agar oddiy kod bo'lsa, input reading qo'shadi
     */
    private String processJavaScriptCode(String code) {
        String trimmedCode = code.trim();
        
        // Agar kod input reading yo'q bo'lsa va o'zgaruvchilar ishlatilgan bo'lsa
        if (needsInputTemplate(trimmedCode)) {
            log.debug("Simple code detected, adding input reading template");
            return addJavaScriptInputTemplate(trimmedCode);
        }
        
        // Aks holda original kodni qaytarish
        return code;
    }
    
    /**
     * Input template kerakligini tekshirish
     */
    private boolean needsInputTemplate(String code) {
        // Input reading allaqachon mavjud bo'lsa, template kerak emas
        if (code.contains("require(") || 
            code.contains("readFileSync") || 
            code.contains("process.stdin") ||
            code.contains("readline") ||
            code.contains("prompt(")) {
            return false;
        }
        
        // Agar kod o'zgaruvchi declaration'larni o'z ichiga olsa, template kerak emas
        if (code.contains("const ") || 
            code.contains("let ") || 
            code.contains("var ")) {
            return false;
        }
        
        // Agar kod o'zgaruvchilarni ishlatsa va input reading yo'q bo'lsa
        return hasVariableUsage(code);
    }
    
    /**
     * Kodda o'zgaruvchilar ishlatilganligini tekshirish
     */
    private boolean hasVariableUsage(String code) {
        // Oddiy regex bilan o'zgaruvchi nomlarini topish
        // Harflar bilan boshlanadigan va raqam/harf bo'lgan so'zlar
        Pattern variablePattern = Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b");
        Matcher matcher = variablePattern.matcher(code);
        
        Set<String> variables = new HashSet<>();
        while (matcher.find()) {
            String word = matcher.group();
            // JavaScript keywords va built-in funksiyalarni ignore qilish
            if (!isJavaScriptKeyword(word)) {
                variables.add(word);
            }
        }
        
        // Agar kamida bitta o'zgaruvchi topilsa
        return !variables.isEmpty();
    }
    
    /**
     * JavaScript keyword yoki built-in funksiya ekanligini tekshirish
     */
    private boolean isJavaScriptKeyword(String word) {
        Set<String> keywords = Set.of(
            // JavaScript keywords
            "break", "case", "catch", "class", "const", "continue", "debugger", "default", 
            "delete", "do", "else", "export", "extends", "finally", "for", "function", 
            "if", "import", "in", "instanceof", "let", "new", "return", "super", "switch", 
            "this", "throw", "try", "typeof", "var", "void", "while", "with", "yield",
            
            // Built-in objects and functions
            "console", "log", "Math", "String", "Number", "Array", "Object", "Date", 
            "RegExp", "Error", "JSON", "parseInt", "parseFloat", "isNaN", "isFinite",
            "undefined", "null", "true", "false", "Infinity", "NaN",
            
            // Common methods
            "toString", "valueOf", "length", "push", "pop", "shift", "unshift", "slice",
            "splice", "join", "split", "reverse", "sort", "indexOf", "includes", "map",
            "filter", "reduce", "forEach", "find", "some", "every", "abs", "max", "min",
            "floor", "ceil", "round", "sqrt", "pow", "random"
        );
        
        return keywords.contains(word);
    }
    
    /**
     * JavaScript uchun input reading template qo'shish
     */
    private String addJavaScriptInputTemplate(String originalCode) {
        // Koddan o'zgaruvchilarni extract qilish
        Set<String> variables = extractVariables(originalCode);
        
        // Kodda declare qilingan o'zgaruvchilarni olib tashlash
        Set<String> declaredVariables = extractDeclaredVariables(originalCode);
        variables.removeAll(declaredVariables);
        
        log.debug("Extracted variables from code '{}': {}", originalCode, variables);
        log.debug("Declared variables in code: {}", declaredVariables);
        
        // Agar input kerak bo'lgan o'zgaruvchilar yo'q bo'lsa, template qo'shmaslik
        if (variables.isEmpty()) {
            return originalCode;
        }
        
        // Template yaratish
        StringBuilder template = new StringBuilder();
        template.append("// Auto-generated input reading template\n");
        
        if (variables.size() <= 2) {
            // 2 ta yoki kamroq o'zgaruvchi bo'lsa - oddiy split
            String varList = String.join(", ", variables);
            template.append("const [").append(varList).append("] = require('fs').readFileSync(0, 'utf8').trim().split(' ').map(Number);\n");
        } else {
            // Ko'proq o'zgaruvchi bo'lsa - har birini alohida o'qish
            template.append("const input = require('fs').readFileSync(0, 'utf8').trim().split('\\n');\n");
            int i = 0;
            for (String var : variables) {
                if (i == 0) {
                    template.append("const ").append(var).append(" = input[0];\n");
                } else {
                    template.append("const ").append(var).append(" = input[").append(i).append("] || input[0].split(' ')[").append(i).append("];\n");
                }
                i++;
            }
        }
        
        template.append("\n// Original code\n");
        template.append(originalCode);
        
        String result = template.toString();
        log.debug("Generated template: {}", result);
        
        return result;
    }
    
    /**
     * Kodda declare qilingan o'zgaruvchilarni extract qilish
     */
    private Set<String> extractDeclaredVariables(String code) {
        Set<String> declaredVars = new HashSet<>();
        
        // const, let, var declarations
        Pattern constPattern = Pattern.compile("\\b(?:const|let|var)\\s+([a-zA-Z_][a-zA-Z0-9_]*)");
        Matcher matcher = constPattern.matcher(code);
        while (matcher.find()) {
            declaredVars.add(matcher.group(1));
        }
        
        // function declarations
        Pattern functionPattern = Pattern.compile("\\bfunction\\s+([a-zA-Z_][a-zA-Z0-9_]*)");
        matcher = functionPattern.matcher(code);
        while (matcher.find()) {
            declaredVars.add(matcher.group(1));
        }
        
        return declaredVars;
    }
    
    /**
     * Koddan o'zgaruvchilarni extract qilish
     */
    private Set<String> extractVariables(String code) {
        // Faqat identifier pattern (harf bilan boshlanadi, harf/raqam davom etadi)
        Pattern variablePattern = Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b");
        Matcher matcher = variablePattern.matcher(code);
        
        Set<String> variables = new LinkedHashSet<>(); // Order muhim
        while (matcher.find()) {
            String word = matcher.group();
            if (!isJavaScriptKeyword(word)) {
                variables.add(word);
            }
        }
        
        return variables;
    }
}