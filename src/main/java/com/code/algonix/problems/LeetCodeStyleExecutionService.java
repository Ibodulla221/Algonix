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
            case "c" -> wrapCCode(userCode, problemId);
            case "csharp", "c#", "cs" -> wrapCSharpCode(userCode, problemId);
            case "go", "golang" -> wrapGoCode(userCode, problemId);
            case "rust", "rs" -> wrapRustCode(userCode, problemId);
            case "php" -> wrapPhpCode(userCode, problemId);
            case "ruby", "rb" -> wrapRubyCode(userCode, problemId);
            case "swift" -> wrapSwiftCode(userCode, problemId);
            case "kotlin", "kt" -> wrapKotlinCode(userCode, problemId);
            case "scala" -> wrapScalaCode(userCode, problemId);
            case "perl", "pl" -> wrapPerlCode(userCode, problemId);
            case "r" -> wrapRCode(userCode, problemId);
            case "dart" -> wrapDartCode(userCode, problemId);
            case "typescript", "ts" -> wrapTypeScriptCode(userCode, problemId);
            case "bash", "sh" -> wrapBashCode(userCode, problemId);
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
                
            default -> // Generic wrapper - user provides complete main method
                """
                import java.util.*;
                import java.io.*;
                
                %s
                """.formatted(userCode);
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
                
            default -> // Generic wrapper - user provides complete script
                """
                import sys
                from typing import List, Optional
                
                %s
                """.formatted(userCode);
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
                
            default -> // Generic wrapper - user provides complete script
                """
                const readline = require('readline');
                const rl = readline.createInterface({
                    input: process.stdin,
                    output: process.stdout
                });
                
                %s
                """.formatted(userCode);
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
                
            default -> // Generic wrapper - user provides complete program
                """
                #include <iostream>
                #include <vector>
                #include <string>
                #include <algorithm>
                #include <map>
                #include <set>
                using namespace std;
                
                %s
                """.formatted(userCode);
        };
    }

    /**
     * C kod wrapping
     */
    private String wrapCCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                #include <stdio.h>
                #include <stdlib.h>
                #include <string.h>
                
                %s
                
                int main() {
                    printf("%%s\\n", helloWorld());
                    return 0;
                }
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                #include <stdio.h>
                #include <stdlib.h>
                #include <string.h>
                
                %s
                
                int main() {
                    int a, b;
                    scanf("%%d %%d", &a, &b);
                    printf("%%d\\n", addTwoNumbers(a, b));
                    return 0;
                }
                """.formatted(userCode);
                
            default -> // Generic wrapper - user provides complete program
                """
                #include <stdio.h>
                #include <stdlib.h>
                #include <string.h>
                
                %s
                """.formatted(userCode);
        };
    }

    /**
     * C# kod wrapping
     */
    private String wrapCSharpCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                using System;
                using System.Collections.Generic;
                using System.Linq;
                
                %s
                
                class Program {
                    static void Main() {
                        Solution solution = new Solution();
                        Console.WriteLine(solution.HelloWorld());
                    }
                }
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                using System;
                using System.Collections.Generic;
                using System.Linq;
                
                %s
                
                class Program {
                    static void Main() {
                        string[] input = Console.ReadLine().Split();
                        int a = int.Parse(input[0]);
                        int b = int.Parse(input[1]);
                        Solution solution = new Solution();
                        Console.WriteLine(solution.AddTwoNumbers(a, b));
                    }
                }
                """.formatted(userCode);
                
            default -> // Generic wrapper - user provides complete program
                """
                using System;
                using System.Collections.Generic;
                using System.Linq;
                using System.Text;
                
                %s
                """.formatted(userCode);
        };
    }

    /**
     * Go kod wrapping
     */
    private String wrapGoCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                package main
                
                import "fmt"
                
                %s
                
                func main() {
                    fmt.Println(helloWorld())
                }
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                package main
                
                import "fmt"
                
                %s
                
                func main() {
                    var a, b int
                    fmt.Scanf("%%d %%d", &a, &b)
                    fmt.Println(addTwoNumbers(a, b))
                }
                """.formatted(userCode);
                
            default -> // Generic wrapper - user provides complete program
                """
                package main
                
                import (
                    "fmt"
                    "bufio"
                    "os"
                    "strconv"
                    "strings"
                )
                
                %s
                """.formatted(userCode);
        };
    }

    /**
     * Rust kod wrapping
     */
    private String wrapRustCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                use std::io;
                
                %s
                
                fn main() {
                    println!("{}", hello_world());
                }
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                use std::io;
                
                %s
                
                fn main() {
                    let mut input = String::new();
                    io::stdin().read_line(&mut input).expect("Failed to read line");
                    let numbers: Vec<i32> = input
                        .trim()
                        .split_whitespace()
                        .map(|s| s.parse().expect("Parse error"))
                        .collect();
                    println!("{}", add_two_numbers(numbers[0], numbers[1]));
                }
                """.formatted(userCode);
                
            default -> // Generic wrapper - user provides complete program
                """
                use std::io;
                use std::collections::HashMap;
                
                %s
                """.formatted(userCode);
        };
    }

    /**
     * PHP kod wrapping
     */
    private String wrapPhpCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                <?php
                %s
                
                echo helloWorld() . "\\n";
                ?>
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                <?php
                %s
                
                $input = trim(fgets(STDIN));
                $numbers = explode(' ', $input);
                $a = intval($numbers[0]);
                $b = intval($numbers[1]);
                echo addTwoNumbers($a, $b) . "\\n";
                ?>
                """.formatted(userCode);
                
            default -> // Generic wrapper - user provides complete script
                """
                <?php
                %s
                ?>
                """.formatted(userCode);
        };
    }

    /**
     * Ruby kod wrapping
     */
    private String wrapRubyCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                %s
                
                puts hello_world
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                %s
                
                a, b = gets.split.map(&:to_i)
                puts add_two_numbers(a, b)
                """.formatted(userCode);
                
            default -> // Generic wrapper - user provides complete script
                userCode;
        };
    }

    /**
     * Swift kod wrapping
     */
    private String wrapSwiftCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                import Foundation
                
                %s
                
                print(helloWorld())
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                import Foundation
                
                %s
                
                let input = readLine()!.split(separator: " ")
                let a = Int(input[0])!
                let b = Int(input[1])!
                print(addTwoNumbers(a, b))
                """.formatted(userCode);
                
            default -> // Generic wrapper - user provides complete program
                """
                import Foundation
                
                %s
                """.formatted(userCode);
        };
    }

    /**
     * Kotlin kod wrapping
     */
    private String wrapKotlinCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                %s
                
                fun main() {
                    println(helloWorld())
                }
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                %s
                
                fun main() {
                    val (a, b) = readLine()!!.split(" ").map { it.toInt() }
                    println(addTwoNumbers(a, b))
                }
                """.formatted(userCode);
                
            default -> // Generic wrapper - user provides complete program
                userCode;
        };
    }

    /**
     * Scala kod wrapping
     */
    private String wrapScalaCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                import scala.io.StdIn
                
                %s
                
                object Main extends App {
                    println(helloWorld())
                }
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                import scala.io.StdIn
                
                %s
                
                object Main extends App {
                    val Array(a, b) = StdIn.readLine().split(" ").map(_.toInt)
                    println(addTwoNumbers(a, b))
                }
                """.formatted(userCode);
                
            default -> // Generic wrapper - user provides complete program
                """
                import scala.io.StdIn
                
                %s
                """.formatted(userCode);
        };
    }

    /**
     * Perl kod wrapping
     */
    private String wrapPerlCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                %s
                
                print hello_world() . "\\n";
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                %s
                
                chomp(my $line = <STDIN>);
                my ($a, $b) = split / /, $line;
                print add_two_numbers($a, $b) . "\\n";
                """.formatted(userCode);
                
            default -> // Generic wrapper - user provides complete script
                userCode;
        };
    }

    /**
     * R kod wrapping
     */
    private String wrapRCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                %s
                
                cat(hello_world(), "\\n")
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                %s
                
                input <- readLines("stdin", n=1)
                numbers <- as.numeric(strsplit(input, " ")[[1]])
                cat(add_two_numbers(numbers[1], numbers[2]), "\\n")
                """.formatted(userCode);
                
            default -> // Generic wrapper - user provides complete script
                userCode;
        };
    }

    /**
     * Dart kod wrapping
     */
    private String wrapDartCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                import 'dart:io';
                
                %s
                
                void main() {
                    print(helloWorld());
                }
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                import 'dart:io';
                
                %s
                
                void main() {
                    List<String> input = stdin.readLineSync()!.split(' ');
                    int a = int.parse(input[0]);
                    int b = int.parse(input[1]);
                    print(addTwoNumbers(a, b));
                }
                """.formatted(userCode);
                
            default -> // Generic wrapper - user provides complete program
                """
                import 'dart:io';
                
                %s
                """.formatted(userCode);
        };
    }

    /**
     * TypeScript kod wrapping
     */
    private String wrapTypeScriptCode(String userCode, Long problemId) {
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
                
                rl.on('line', (line: string) => {
                    const [a, b] = line.split(' ').map(Number);
                    console.log(addTwoNumbers(a, b));
                    rl.close();
                });
                """.formatted(userCode);
                
            default -> // Generic wrapper - user provides complete script
                """
                const readline = require('readline');
                const rl = readline.createInterface({
                    input: process.stdin,
                    output: process.stdout
                });
                
                %s
                """.formatted(userCode);
        };
    }

    /**
     * Bash kod wrapping
     */
    private String wrapBashCode(String userCode, Long problemId) {
        return switch (problemId.intValue()) {
            case 1 -> // Hello World
                """
                #!/bin/bash
                %s
                
                hello_world
                """.formatted(userCode);
                
            case 2 -> // Add Two Numbers
                """
                #!/bin/bash
                %s
                
                read -r a b
                add_two_numbers $a $b
                """.formatted(userCode);
                
            default -> // Generic wrapper - user provides complete script
                """
                #!/bin/bash
                %s
                """.formatted(userCode);
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