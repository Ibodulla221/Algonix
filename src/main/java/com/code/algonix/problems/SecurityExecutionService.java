package com.code.algonix.problems;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Kod bajarish uchun qo'shimcha xavfsizlik choralari
 */
@Service
@Slf4j
public class SecurityExecutionService {

    /**
     * Vaqtinchalik papkani xavfsiz qilish
     */
    public void secureWorkDirectory(Path workDir) throws IOException {
        try {
            // Unix/Linux tizimlarida faqat owner uchun ruxsat
            Set<PosixFilePermission> permissions = Set.of(
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.OWNER_EXECUTE
            );
            Files.setPosixFilePermissions(workDir, permissions);
        } catch (UnsupportedOperationException e) {
            // Windows tizimida POSIX permissions qo'llab-quvvatlanmaydi
            log.debug("POSIX permissions not supported on this system");
        }
    }

    /**
     * Jarayonni xavfsiz bajarish
     */
    public CompletableFuture<Process> executeSecurely(ProcessBuilder pb, long timeoutMs) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Process process = pb.start();
                
                // Timeout bilan kutish
                if (!process.waitFor(timeoutMs, TimeUnit.MILLISECONDS)) {
                    process.destroyForcibly();
                    throw new RuntimeException("Process timed out");
                }
                
                return process;
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Process execution failed", e);
            }
        });
    }

    /**
     * Resurs ishlatishini monitoring qilish
     */
    public void monitorResourceUsage(Process process) {
        ProcessHandle handle = process.toHandle();
        
        // CPU va memory monitoring (Java 9+)
        handle.info().totalCpuDuration().ifPresent(duration -> {
            if (duration.toMillis() > 30000) { // 30 soniya
                log.warn("Process using too much CPU time: {} ms", duration.toMillis());
                process.destroyForcibly();
            }
        });
    }

    /**
     * Fayl tizimini tozalash
     */
    public void cleanupSecurely(Path workDir) {
        try {
            Files.walk(workDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try {
                            // Faylni o'qish-yozish ruxsatini olib tashlash
                            try {
                                Files.setPosixFilePermissions(path, Set.of());
                            } catch (UnsupportedOperationException ignored) {
                                // Windows uchun
                            }
                            Files.delete(path);
                        } catch (IOException e) {
                            log.warn("Failed to delete: {}", path, e);
                        }
                    });
        } catch (IOException e) {
            log.warn("Failed to cleanup directory: {}", workDir, e);
        }
    }

    /**
     * Kod ichidagi xavfli pattern'larni aniqlash
     */
    public boolean isCodeSafe(String code, String language) {
        // Til-specific xavfli pattern'lar
        return switch (language.toLowerCase()) {
            case "java" -> isJavaCodeSafe(code);
            case "python", "py" -> isPythonCodeSafe(code);
            case "javascript", "js" -> isJavaScriptCodeSafe(code);
            case "cpp", "c++" -> isCppCodeSafe(code);
            case "c" -> isCCodeSafe(code);
            default -> isGeneralCodeSafe(code);
        };
    }

    private boolean isJavaCodeSafe(String code) {
        String[] javaUnsafe = {
            "Runtime.getRuntime()", "ProcessBuilder", "System.exit",
            "File.delete", "Files.delete", "FileWriter", "FileOutputStream",
            "Socket", "ServerSocket", "URL", "URLConnection",
            "Class.forName", "Method.invoke", "Field.set"
        };
        return !containsAny(code, javaUnsafe);
    }

    private boolean isPythonCodeSafe(String code) {
        String[] pythonUnsafe = {
            "import os", "import subprocess", 
            "exec(", "eval(", "__import__", "open(",
            "file(", "raw_input(", "compile(",
            "globals(", "locals(", "vars(", "dir("
        };
        return !containsAny(code, pythonUnsafe);
    }

    private boolean isJavaScriptCodeSafe(String code) {
        String[] jsUnsafe = {
            "import(", "eval(", "Function(",
            "setTimeout", "setInterval", "process.exit",
            "path.", "os.", "child_process",
            "http.", "https.", "net.", "url."
        };
        return !containsAny(code, jsUnsafe);
    }

    private boolean isCppCodeSafe(String code) {
        String[] cppUnsafe = {
            "#include <cstdlib>", "#include <system>", "system(",
            "exec(", "fork(", "clone(", "exit(",
            "abort(", "terminate(", "quick_exit("
        };
        return !containsAny(code, cppUnsafe);
    }

    private boolean isCCodeSafe(String code) {
        String[] cUnsafe = {
            "#include <stdlib.h>", "#include <unistd.h>", "system(",
            "exec(", "fork(", "exit(", "abort(", "malloc("
        };
        return !containsAny(code, cUnsafe);
    }

    private boolean isGeneralCodeSafe(String code) {
        String[] generalUnsafe = {
            "http://", "https://", "ftp://", "file://",
            "../", "..\\", "/etc/", "C:\\Windows",
            "rm ", "del ", "format ", "shutdown", "reboot"
        };
        return !containsAny(code, generalUnsafe);
    }

    private boolean containsAny(String code, String[] patterns) {
        String lowerCode = code.toLowerCase();
        for (String pattern : patterns) {
            if (lowerCode.contains(pattern.toLowerCase())) {
                log.warn("Xavfli pattern aniqlandi: {}", pattern);
                return true;
            }
        }
        return false;
    }
}