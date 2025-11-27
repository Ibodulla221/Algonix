package com.code.algonix.problems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodeExecutionServiceTest {

    @Mock
    private DockerExecutionHelper dockerHelper;

    @InjectMocks
    private CodeExecutionService codeExecutionService;

    private TestCase testCase;

    @BeforeEach
    void setUp() {
        testCase = TestCase.builder()
                .id(1L)
                .input("2 3")
                .expectedOutput("5")
                .isHidden(false)
                .timeLimitMs(2000)
                .build();
    }

    @Test
    void executeCode_CompilationError_ReturnsCompileError() throws Exception {
        // Given
        String code = "invalid java code";
        String language = "java";

        when(dockerHelper.sourceFileNameFor(language)).thenReturn("Main.java");
        when(dockerHelper.compileIfNeeded(eq(language), any()))
                .thenReturn(new DockerExecutionHelper.ExecutionResult(
                        false, "", "Compilation error", 0));

        // When
        CodeExecutionService.ExecutionResult result = codeExecutionService.executeCode(
                code, language, List.of(testCase));

        // Then
        assertEquals(CodeExecutionService.ExecutionStatus.COMPILE_ERROR, result.getStatus());
        assertNotNull(result.getErrorMessage());
        verify(dockerHelper, times(1)).compileIfNeeded(eq(language), any());
        verify(dockerHelper, never()).runExecutable(any(), any(), any(), anyLong());
    }

    @Test
    void executeCode_SuccessfulExecution_ReturnsAccepted() throws Exception {
        // Given
        String code = "public class Main { public static void main(String[] args) { System.out.println(\"5\"); } }";
        String language = "java";

        when(dockerHelper.sourceFileNameFor(language)).thenReturn("Main.java");
        when(dockerHelper.compileIfNeeded(eq(language), any()))
                .thenReturn(new DockerExecutionHelper.ExecutionResult(true, "", "", 0));
        when(dockerHelper.runExecutable(eq(language), any(), anyString(), anyLong()))
                .thenReturn(new DockerExecutionHelper.ExecutionResult(true, "5", "", 100));

        // When
        CodeExecutionService.ExecutionResult result = codeExecutionService.executeCode(
                code, language, List.of(testCase));

        // Then
        assertEquals(CodeExecutionService.ExecutionStatus.ACCEPTED, result.getStatus());
        assertEquals(1, result.getPassedTestCases());
        assertEquals(1, result.getTotalTestCases());
        assertTrue(result.getTestResults().get(0).isPassed());
    }
}
