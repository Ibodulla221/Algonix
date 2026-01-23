package com.code.algonix.problems;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * LeetCode style kod bajarish - faqat funksiya'lar uchun
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LeetCodeStyleExecutionService {

    private final SimpleJudgeService simpleJudgeService;

    /**
     * LeetCode style kodini to'liq dasturga aylantirish va bajarish
     */
    public CodeExecutionService.ExecutionResult executeFunction(String userCode, String language, 
                                                               List<TestCase> testCases, Long problemId) {
        
        // User kodini to'liq dasturga aylantirish
        String fullCode = wrapUserCode(userCode, language, problemId);
        
        if (fullCode == null) {
            return createErrorResult(CodeExecutionService.ExecutionStatus.COMPILE_ERROR, 
                "Qo'llab-quvvatlanmaydigan til yoki masala");
        }
        
        // SimpleJudgeService orqali bajarish
        return simpleJudgeService.executeCode(fullCode, language, testCases);
    }

    /**
     * User kodini to'liq dasturga aylantirish
     */
    private String wrapUserCode(String userCode, String language, Long problemId) {
        return switch (language.toLowerCase()) {
            case "java" -> wrapJavaCode(userCode, problemId);
            case "python", "python3", "py" -> wrapPythonCode(userCode, problemId);
            case "javascript", "js" -> wrapJavaScriptCode(userCode, problemId);
            case "cpp", "c++" -> wrapCppCode(userCode, problemId);
            default -> null;
        };
    }

    /**
     * Java kod wrapping
     */
    private String wrapJavaCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                import java.util.*;
                
                %s
                
                public class Main {
                    public static void main(String[] args) {
                        Solution solution = new Solution();
                        System.out.println(solution.helloWorld());
                    }
                }
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                import java.util.*;
                
                %s
                
                public class Main {
                    public static void main(String[] args) {
                        Scanner sc = new Scanner(System.in);
                        int a = sc.nextInt();
                        int b = sc.nextInt();
                        Solution solution = new Solution();
                        System.out.println(solution.addTwoNumbers(a, b));
                        sc.close();
                    }
                }
                """.formatted(userCode);
                
            case 6 -> // Array Sum
                """
                import java.util.*;
                
                %s
                
                public class Main {
                    public static void main(String[] args) {
                        Scanner sc = new Scanner(System.in);
                        int n = sc.nextInt();
                        int[] nums = new int[n];
                        for (int i = 0; i < n; i++) {
                            nums[i] = sc.nextInt();
                        }
                        Solution solution = new Solution();
                        System.out.println(solution.arraySum(nums));
                        sc.close();
                    }
                }
                """.formatted(userCode);
                
            default -> null;
        };
    }

    /**
     * Python kod wrapping
     */
    private String wrapPythonCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                %s
                
                solution = Solution()
                print(solution.hello_world())
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                %s
                
                a, b = map(int, input().split())
                solution = Solution()
                print(solution.add_two_numbers(a, b))
                """.formatted(userCode);
                
            case 6 -> // Array Sum
                """
                from typing import List
                
                %s
                
                n = int(input())
                nums = list(map(int, input().split()))
                solution = Solution()
                print(solution.array_sum(nums))
                """.formatted(userCode);
                
            default -> null;
        };
    }

    /**
     * JavaScript kod wrapping
     */
    private String wrapJavaScriptCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                %s
                
                console.log(helloWorld());
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                const readline = require('readline');
                const rl = readline.createInterface({
                    input: process.stdin,
                    output: process.stdout
                });
                
                %s
                
                rl.on('line', (line) => {
                    const [a, b] = line.split(' ').map(Number);
                    console.log(addTwoNumbers(a, b));
                    rl.close();
                });
                """.formatted(userCode);
                
            case 6 -> // Array Sum
                """
                const readline = require('readline');
                const rl = readline.createInterface({
                    input: process.stdin,
                    output: process.stdout
                });
                
                %s
                
                let lineCount = 0;
                let n, nums;
                
                rl.on('line', (line) => {
                    if (lineCount === 0) {
                        n = parseInt(line);
                    } else {
                        nums = line.split(' ').map(Number);
                        console.log(arraySum(nums));
                        rl.close();
                    }
                    lineCount++;
                });
                """.formatted(userCode);
                
            default -> null;
        };
    }

    /**
     * C++ kod wrapping
     */
    private String wrapCppCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                #include <iostream>
                #include <vector>
                #include <string>
                using namespace std;
                
                %s
                
                int main() {
                    Solution solution;
                    cout << solution.helloWorld() << endl;
                    return 0;
                }
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                #include <iostream>
                #include <vector>
                #include <string>
                using namespace std;
                
                %s
                
                int main() {
                    int a, b;
                    cin >> a >> b;
                    Solution solution;
                    cout << solution.addTwoNumbers(a, b) << endl;
                    return 0;
                }
                """.formatted(userCode);
                
            case 6 -> // Array Sum
                """
                #include <iostream>
                #include <vector>
                #include <string>
                using namespace std;
                
                %s
                
                int main() {
                    int n;
                    cin >> n;
                    vector<int> nums(n);
                    for (int i = 0; i < n; i++) {
                        cin >> nums[i];
                    }
                    Solution solution;
                    cout << solution.arraySum(nums) << endl;
                    return 0;
                }
                """.formatted(userCode);
                
            default -> null;
        };
    }

    private CodeExecutionService.ExecutionResult createErrorResult(CodeExecutionService.ExecutionStatus status, String message) {
        return CodeExecutionService.ExecutionResult.builder()
            .status(status)
            .errorMessage(message)
            .testResults(List.of())
            .totalTestCases(0)
            .passedTestCases(0)
            .averageRuntime(0)
            .averageMemory(0.0)
            .build();
    }
}