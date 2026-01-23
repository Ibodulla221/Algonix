package com.code.algonix.problems;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodeExecutionServiceSelector {

    private final NativeCodeExecutionService nativeExecutionService;
    private final Judge0ExecutionService judge0ExecutionService;
    private final MultiLanguageExecutionService multiLanguageExecutionService;
    private final SimpleJudgeService simpleJudgeService;
    private final LeetCodeExecutionService leetCodeExecutionService;

    @Value("${code.execution.use-judge0:false}")
    private boolean useJudge0;

    @Value("${code.execution.use-native:false}")
    private boolean useNative;

    @Value("${code.execution.use-multi-language:false}")
    private boolean useMultiLanguage;
    
    @Value("${code.execution.use-simple-judge:false}")
    private boolean useSimpleJudge;
    
    @Value("${code.execution.use-leetcode:true}")
    private boolean useLeetCode;

    public CodeExecutionService.ExecutionResult executeCode(String code, String language, List<TestCase> testCases) {
        if (useLeetCode) {
            return leetCodeExecutionService.executeCode(code, language, testCases);
        } else if (useSimpleJudge) {
            return simpleJudgeService.executeCode(code, language, testCases);
        } else if (useMultiLanguage) {
            return multiLanguageExecutionService.executeCode(code, language, testCases);
        } else if (useJudge0) {
            return judge0ExecutionService.executeCode(code, language, testCases);
        } else if (useNative) {
            return nativeExecutionService.executeCode(code, language, testCases);
        } else {
            // Default to LeetCode
            return leetCodeExecutionService.executeCode(code, language, testCases);
        }
    }

    public String getExecutionMethod() {
        if (useLeetCode) {
            return "LeetCode Style (Function-only execution with auto-wrapping)";
        } else if (useSimpleJudge) {
            return "Simple Judge (kep.uz kabi - C++, C, Java, Python, JavaScript)";
        } else if (useMultiLanguage) {
            return "Multi-Language (JavaScript, Python, Java, C++, PHP)";
        } else if (useJudge0) {
            return "Judge0 API";
        } else if (useNative) {
            return "Native ProcessBuilder";
        } else {
            return "LeetCode Style (Default)";
        }
    }
}