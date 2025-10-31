package com.code.algonix.problems;

import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

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
        return switch (language.toLowerCase()) {
            case "java" -> "Main.java";
            case "c++", "cpp" -> "Main.cpp";
            case "c" -> "Main.c";
            case "python3", "python" -> "main.py";
            case "javascript", "node", "js" -> "main.js";
            default -> "main.txt";
        };
    }

    // 🔹 Kerak bo‘lsa kompilyatsiya qilish
    public ExecutionResult compileIfNeeded(String language, Path workDir) throws Exception {
        String lang = language.toLowerCase();
        if (lang.equals("java")) {
            return execInDockerAndCapture("openjdk:21-jdk", workDir,
                    new String[]{"bash", "-c", "javac Main.java"}, 15000, null);
        }
        if (lang.equals("c") || lang.equals("cpp") || lang.equals("c++")) {
            String cmd = lang.equals("c")
                    ? "gcc Main.c -O2 -o Main"
                    : "g++ Main.cpp -O2 -std=gnu++17 -o Main";
            return execInDockerAndCapture("gcc:12", workDir,
                    new String[]{"bash", "-c", cmd}, 20000, null);
        }
        return new ExecutionResult(true, "", "", 0); // interpreted tillar uchun
    }

    // 🔹 Dasturni ishga tushirish
    public ExecutionResult runExecutable(String language, Path workDir, String stdin, long timeoutMs) throws Exception {
        String lang = language.toLowerCase();

        if (lang.equals("java")) {
            return execInDockerAndCapture("openjdk:21-jdk", workDir,
                    new String[]{"bash", "-c", "java Main"}, timeoutMs, stdin);
        }
        if (lang.equals("python3") || lang.equals("python")) {
            return execInDockerAndCapture("python:3.11", workDir,
                    new String[]{"bash", "-c", "python3 main.py"}, timeoutMs, stdin);
        }
        if (lang.equals("cpp") || lang.equals("c++") || lang.equals("c")) {
            return execInDockerAndCapture("gcc:12", workDir,
                    new String[]{"bash", "-c", "./Main"}, timeoutMs, stdin);
        }
        if (lang.equals("javascript") || lang.equals("node") || lang.equals("js")) {
            return execInDockerAndCapture("node:20", workDir,
                    new String[]{"bash", "-c", "node main.js"}, timeoutMs, stdin);
        }

        return new ExecutionResult(false, "", "Unsupported language: " + language, 0);
    }

    // 🔹 Docker ichida bajarish (asosiy ishchi metod)
    private ExecutionResult execInDockerAndCapture(String image, Path workDir, String[] command,
                                                   long timeoutMs, String stdin) throws Exception {

        List<String> dockerCmd = new ArrayList<>(List.of(
                "docker", "run", "--rm", "-i", // 🔸 -i: stdin ochiq bo‘lsin
                "--network", "none",
                "--cpus", "0.5",
                "--memory", "256m",
                "--pids-limit", "64",
                "--security-opt", "no-new-privileges",
                "-v", workDir.toAbsolutePath() + ":/work",
                "-w", "/work",
                image
        ));
        dockerCmd.addAll(List.of(command));

        ProcessBuilder pb = new ProcessBuilder(dockerCmd);
        pb.redirectErrorStream(false);
        Process process = pb.start();

        // 🔸 stdin uzatish (Scanner uchun)
        if (stdin != null && !stdin.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(stdin);
                if (!stdin.endsWith("\n")) writer.newLine();
                writer.flush();
            } catch (Exception ignored) {}
        }

        // 🔸 stdout va stderr oqimlarini o‘qish uchun parallel thread
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();

        Thread outThread = new Thread(() -> readStream(process.getInputStream(), outStream));
        Thread errThread = new Thread(() -> readStream(process.getErrorStream(), errStream));
        outThread.start();
        errThread.start();

        long start = System.currentTimeMillis();
        boolean finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
        long end = System.currentTimeMillis();

        if (!finished) {
            process.destroyForcibly();
            return new ExecutionResult(false, outStream.toString(), "TIMEOUT", end - start);
        }

        outThread.join();
        errThread.join();

        String stdout = outStream.toString().trim();
        String stderr = errStream.toString().trim();

        int exitCode = process.exitValue();
        boolean success = (exitCode == 0);

        return new ExecutionResult(success, stdout, stderr, end - start);
    }

    // 🔹 Stream o‘qish yordamchisi
    private static void readStream(InputStream is, OutputStream out) {
        try (is; out) {
            is.transferTo(out);
        } catch (IOException ignored) {}
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
