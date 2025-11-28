package com.code.algonix.problems;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public class DockerExecutionHelper {

    public static class ExecutionResult {
        private final boolean success;
        private final String stdOut;
        private final String stdErr;
        private final long timeMs;

        public ExecutionResult(boolean success, String stdOut, String stdErr, long timeMs) {
            this.success = success;
            this.stdOut = stdOut;
            this.stdErr = stdErr;
            this.timeMs = timeMs;
        }

        public boolean isSuccess() { return success; }
        public String getStdOut() { return stdOut; }
        public String getStdErr() { return stdErr; }
        public long getTimeMs() { return timeMs; }
    }

    // 🔹 Fayl nomini til bo‘yicha aniqlash
    public String sourceFileNameFor(String language) {
        return switch (language == null ? "" : language.toLowerCase()) {
            case "java" -> "Main.java";
            case "c++", "cpp" -> "Main.cpp";
            case "c" -> "Main.c";
            case "python3", "python" -> "main.py";
            case "javascript", "node", "js" -> "main.js";
            case "typescript", "ts" -> "main.ts";
            case "go" -> "main.go";
            case "kotlin" -> "Main.kt";
            case "swift" -> "main.swift";
            case "rust" -> "main.rs";
            case "ruby" -> "main.rb";
            case "php" -> "main.php";
            case "dart" -> "main.dart";
            case "   scala" -> "Main.scala";
            case "elixir" -> "main.exs";
            case "erlang" -> "main.erl";
            case "racket" -> "main.rkt";
            case "c#", "csharp" -> "Main.cs";
            default -> "main.txt";
        };
    }

    // 🔹 Kerak bo‘lsa kompilyatsiya qilish
    public ExecutionResult compileIfNeeded(String language, Path workDir) throws Exception {
        String lang = language == null ? "" : language.toLowerCase();
        if (lang.equals("java")) {
            return execInDockerAndCapture("openjdk:21", workDir,
                    new String[]{"bash", "-c", "javac Main.java"}, 15000, null);
        }
        if (lang.equals("c") || lang.equals("cpp") || lang.equals("c++")) {
            String cmd = lang.equals("c")
                    ? "gcc Main.c -O2 -o Main"
                    : "g++ Main.cpp -O2 -std=gnu++17 -o Main";
            return execInDockerAndCapture("gcc:latest", workDir,
                    new String[]{"bash", "-c", cmd}, 30000, null);
        }
        if (lang.equals("go")) {
            return execInDockerAndCapture("golang:1.21-alpine", workDir,
                    new String[]{"bash", "-c", "go build -o Main main.go"}, 20000, null);
        }
        if (lang.equals("rust")) {
            return execInDockerAndCapture("rust:1.75-slim", workDir,
                    new String[]{"bash", "-c", "rustc main.rs -O -o Main"}, 30000, null);
        }
        if (lang.equals("c#") || lang.equals("csharp")) {
            // C# doesn't need compilation for simple scripts
            return new ExecutionResult(true, "", "", 0);
        }
        if (lang.equals("typescript") || lang.equals("ts")) {
            return execInDockerAndCapture("node:18-slim", workDir,
                    new String[]{"bash", "-c", "npm install -g typescript && tsc main.ts"}, 60000, null);
        }
        if (lang.equals("kotlin")) {
            return execInDockerAndCapture("openjdk:21", workDir,
                    new String[]{"bash", "-c", "kotlinc Main.kt -include-runtime -d Main.jar"}, 60000, null);
        }
        if (lang.equals("scala")) {
            // Scala needs to be installed, skip compilation for now
            return new ExecutionResult(true, "", "", 0);
        }
        if (lang.equals("swift")) {
            return execInDockerAndCapture("swift:5.9", workDir,
                    new String[]{"bash", "-c", "swiftc main.swift -o Main"}, 60000, null);
        }
        // boshqa tillar uchun odatda compile shart emas; qaytar true
        return new ExecutionResult(true, "", "", 0);
    }

    // 🔹 Dasturni ishga tushirish
    public ExecutionResult runExecutable(String language, Path workDir, String stdin, long timeoutMs) throws Exception {
        String lang = language == null ? "" : language.toLowerCase();

        if (lang.equals("java")) {
            return execInDockerAndCapture("openjdk:21", workDir,
                    new String[]{"bash", "-c", "java Main"}, timeoutMs, stdin);
        }
        if (lang.equals("python3") || lang.equals("python")) {
            return execInDockerAndCapture("python:3.11-slim", workDir,
                    new String[]{"bash", "-c", "python3 main.py"}, timeoutMs, stdin);
        }
        if (lang.equals("cpp") || lang.equals("c++") || lang.equals("c")) {
            return execInDockerAndCapture("gcc:latest", workDir,
                    new String[]{"bash", "-c", "./Main"}, timeoutMs, stdin);
        }
        if (lang.equals("javascript") || lang.equals("node") || lang.equals("js")) {
            return execInDockerAndCapture("node:18-slim", workDir,
                    new String[]{"bash", "-c", "node main.js"}, timeoutMs, stdin);
        }
        if (lang.equals("typescript") || lang.equals("ts")) {
            // after tsc -> run via node
            return execInDockerAndCapture("node:18-slim", workDir,
                    new String[]{"bash", "-c", "node main.js"}, timeoutMs, stdin);
        }
        if (lang.equals("go")) {
            return execInDockerAndCapture("golang:1.21-alpine", workDir,
                    new String[]{"bash", "-c", "./Main"}, timeoutMs, stdin);
        }
        if (lang.equals("kotlin")) {
            return execInDockerAndCapture("openjdk:21", workDir,
                    new String[]{"bash", "-c", "java -jar Main.jar"}, timeoutMs, stdin);
        }
        if (lang.equals("swift")) {
            return execInDockerAndCapture("swift:5.9", workDir,
                    new String[]{"bash", "-c", "./Main"}, timeoutMs, stdin);
        }
        if (lang.equals("rust")) {
            return execInDockerAndCapture("rust:1.75-slim", workDir,
                    new String[]{"bash", "-c", "./Main"}, timeoutMs, stdin);
        }
        if (lang.equals("ruby")) {
            return execInDockerAndCapture("ruby:3.2-slim", workDir,
                    new String[]{"bash", "-c", "ruby main.rb"}, timeoutMs, stdin);
        }
        if (lang.equals("php")) {
            return execInDockerAndCapture("php:8.2-cli", workDir,
                    new String[]{"bash", "-c", "php main.php"}, timeoutMs, stdin);
        }
        if (lang.equals("dart")) {
            return execInDockerAndCapture("dart:stable", workDir,
                    new String[]{"bash", "-c", "dart run main.dart"}, timeoutMs, stdin);
        }
        if (lang.equals("scala")) {
            // Scala runtime not available, return error
            return new ExecutionResult(false, "", "Scala runtime not configured", 0);
        }
        if (lang.equals("elixir")) {
            return execInDockerAndCapture("elixir:1.16-slim", workDir,
                    new String[]{"bash", "-c", "elixir main.exs"}, timeoutMs, stdin);
        }
        if (lang.equals("erlang")) {
            // assumes a module 'main' with -module(main). -export([start/0]). start() -> io:fwrite...
            return execInDockerAndCapture("erlang:26", workDir,
                    new String[]{"bash", "-c", "erl -noshell -s main start -s init stop"}, timeoutMs, stdin);
        }
        if (lang.equals("racket")) {
            return execInDockerAndCapture("racket/racket:8.11", workDir,
                    new String[]{"bash", "-c", "racket main.rkt"}, timeoutMs, stdin);
        }
        if (lang.equals("c#") || lang.equals("csharp")) {
            // run via dotnet script
            return execInDockerAndCapture("mcr.microsoft.com/dotnet/sdk:7.0", workDir,
                    new String[]{"bash", "-c", "dotnet script Main.cs"}, timeoutMs, stdin);
        }

        return new ExecutionResult(false, "", "Unsupported language: " + language, 0);
    }

    // 🔹 Docker ichida bajarish (asosiy ishchi metod)
    private ExecutionResult execInDockerAndCapture(String image, Path workDir, String[] command,
                                                   long timeoutMs, String stdin) throws Exception {

        List<String> dockerCmd = new ArrayList<>(Arrays.asList(
                "docker", "run", "--rm", "-i", // -i to keep stdin open
                "--network", "none",
                "--cpus", "0.5",
                "--memory", "256m",
                "--pids-limit", "64",
                "--security-opt", "no-new-privileges",
                "-v", workDir.toAbsolutePath() + ":/work",
                "-w", "/work",
                image
        ));
        dockerCmd.addAll(Arrays.asList(command));

        ProcessBuilder pb = new ProcessBuilder(dockerCmd);
        // do not merge error into output; we want both streams separately
        pb.redirectErrorStream(false);
        Process process = pb.start();

        // 🔸 stdin uzatish (Scanner uchun)
        if (stdin != null && !stdin.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(stdin);
                if (!stdin.endsWith("\n")) writer.newLine();
                writer.flush();
            } catch (Exception ignored) {}
        } else {
            // close stdin to signal EOF for programs expecting EOF
            try {
                process.getOutputStream().close();
            } catch (IOException ignored) {}
        }

        // 🔸 stdout va stderr oqimlarini o‘qish uchun parallel thread
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();

        Thread outThread = new Thread(() -> copyStream(process.getInputStream(), outStream));
        Thread errThread = new Thread(() -> copyStream(process.getErrorStream(), errStream));
        outThread.start();
        errThread.start();

        long start = System.currentTimeMillis();
        boolean finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
        long end = System.currentTimeMillis();

        if (!finished) {
            process.destroyForcibly();
            // try to join threads to get partial output
            outThread.join(200);
            errThread.join(200);
            return new ExecutionResult(false, outStream.toString().trim(), "TIMEOUT", end - start);
        }

        outThread.join();
        errThread.join();

        String stdout = outStream.toString().trim();
        String stderr = errStream.toString().trim();

        int exitCode = process.exitValue();
        boolean success = (exitCode == 0);

        return new ExecutionResult(success, stdout, stderr, end - start);
    }

    // 🔹 Stream o‘qish yordamchisi (doimiy o‘qiydi va yozadi, lekin streamlarni yopmaydi)
    private static void copyStream(InputStream is, OutputStream out) {
        try {
            byte[] buf = new byte[4096];
            int r;
            while ((r = is.read(buf)) != -1) {
                out.write(buf, 0, r);
            }
            out.flush();
        } catch (IOException ignored) {
        }
    }

    // 🔹 Temp papkani tozalash
    public void cleanupTempDir(Path dir) throws IOException {
        if (dir == null || !Files.exists(dir)) return;
        Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}
