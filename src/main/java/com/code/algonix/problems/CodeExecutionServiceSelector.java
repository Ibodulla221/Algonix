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

    @Value("${code.execution.use-judge0:false}")
    private boolean useJudge0;

    @Value("${code.execution.use-native:false}")
    private boolean useNative;

    @Value("${code.execution.use-multi-language:true}")
    private boolean useMultiLanguage;

    public CodeExecutionService.ExecutionResult executeCode(String code, String language, List<TestCase> testCases) {
        if (useMultiLanguage) {
            return multiLanguageExecutionService.executeCode(code, language, testCases);
        } else if (useJudge0) {
            return judge0ExecutionService.executeCode(code, language, testCases);
        } else if (useNative) {
            return nativeExecutionService.executeCode(code, language, testCases);
        } else {
            // Default to simplified multi-language
            return multiLanguageExecutionService.executeCode(code, language, testCases);
        }
    }

    public String getExecutionMethod() {
        if (useMultiLanguage) {
            return "Simplified Multi-Language (JavaScript, Python, Java, C++, PHP)";
        } else if (useJudge0) {
            return "Judge0 API";
        } else if (useNative) {
            return "Native ProcessBuilder (3 til)";
        } else {
            return "Simplified Multi-Language (Default)";
        }
    }
}