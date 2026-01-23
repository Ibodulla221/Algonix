# Algonix Code Execution System

## 🏗️ Arxitektura

Algonix'da kod bajarish tizimi **MultiLanguageExecutionService** orqali amalga oshirilgan.

### Asosiy komponentlar:

1. **MultiLanguageExecutionService** - Asosiy kod bajarish xizmati
2. **SecurityExecutionService** - Xavfsizlik choralari
3. **ResourceMonitoringService** - Resurs monitoring
4. **Judge0ExecutionService** - Tashqi API (ixtiyoriy)

## 🔒 Xavfsizlik choralari

### 1. Kod validatsiya
```java
// Xavfli komandalarni tekshirish
if (!securityService.isCodeSafe(code, language)) {
    return createErrorResult("Xavfli komandalar aniqlandi");
}
```

### 2. Sandbox environment
```java
// Vaqtinchalik papka yaratish
Path workDir = Files.createTempDirectory("algonix-simple-");
securityService.secureWorkDirectory(workDir);
```

### 3. Resource limits
```java
@Value("${code.execution.timeout-ms:10000}")
private long timeoutMs;

@Value("${code.execution.memory-limit:128}")
private int memoryLimitMB;

@Value("${code.execution.max-file-size:65536}")
private int maxFileSize;
```

## 🚀 Qo'llab-quvvatlanadigan tillar

### Native execution:
- **JavaScript** (Node.js)
- **Python** 3.x
- **Java** 17+
- **C++** (g++)
- **PHP** 8.x

### Kengaytirish mumkin:
- C, C#, Go, Rust
- Ruby, Perl, Scala
- Kotlin, Swift

## 📝 Kod bajarish jarayoni

### 1. Pre-processing
```java
// Kod hajmini tekshirish
if (code.length() > maxFileSize) {
    return createErrorResult("Kod hajmi juda katta");
}

// Tizim resurslarini tekshirish
if (!resourceMonitoringService.hasEnoughResources()) {
    return createErrorResult("Tizim resurslari yetarli emas");
}
```

### 2. Compilation (agar kerak bo'lsa)
```java
// Java uchun
ProcessBuilder compileBuilder = new ProcessBuilder("javac", javaFile.toString());
Process compileProcess = compileBuilder.start();

if (compileProcess.exitValue() != 0) {
    String error = readProcessOutput(compileProcess.getErrorStream());
    return createCompileErrorResult(error);
}
```

### 3. Execution va testing
```java
for (TestCase testCase : testCases) {
    ProcessBuilder pb = new ProcessBuilder(command);
    pb.directory(workDir.toFile());
    
    Process process = pb.start();
    
    // Input berish
    try (PrintWriter writer = new PrintWriter(process.getOutputStream())) {
        writer.println(testCase.getInput());
        writer.flush();
    }
    
    // Timeout bilan kutish
    boolean finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
    
    if (!finished) {
        process.destroyForcibly();
        return createTimeoutResult();
    }
    
    // Output olish va taqqoslash
    String actualOutput = readProcessOutput(process.getInputStream());
    if (!actualOutput.trim().equals(testCase.getExpectedOutput().trim())) {
        return createWrongAnswerResult(testCase, actualOutput);
    }
}
```

### 4. Cleanup
```java
finally {
    if (workDir != null) {
        securityService.cleanupSecurely(workDir);
    }
}
```

## 🎯 Test case tekshirish

### Exact match
```java
if (!actualOutput.trim().equals(expectedOutput.trim())) {
    return "WRONG_ANSWER";
}
```

### Custom checker (kelajakda)
```java
// Special judge uchun
public boolean customCheck(String input, String output, String expected) {
    // Maxsus tekshirish logikasi
    return checkFloatingPoint(output, expected, 1e-6);
}
```

## 📊 Natija turlari

- **ACCEPTED** - To'g'ri javob
- **WRONG_ANSWER** - Noto'g'ri javob
- **TIME_LIMIT_EXCEEDED** - Vaqt tugadi
- **MEMORY_LIMIT_EXCEEDED** - Xotira tugadi
- **RUNTIME_ERROR** - Runtime xatosi
- **COMPILE_ERROR** - Compile xatosi
- **PENDING** - Kutilmoqda

## 🔧 Konfiguratsiya

```properties
# Code execution settings
code.execution.timeout-ms=10000
code.execution.memory-limit=64
code.execution.max-output-size=1048576
code.execution.max-file-size=65536
code.execution.use-native=true
code.execution.use-judge0=false
```

## 🚀 Kelajakdagi yaxshilashlar

### 1. Docker integration
```yaml
version: '3'
services:
  code-runner:
    image: algonix/code-runner
    memory: 128m
    cpus: 0.5
    network_mode: none
```

### 2. Kubernetes pods
```yaml
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: code-runner
    image: node:alpine
    resources:
      limits:
        memory: "128Mi"
        cpu: "500m"
```

### 3. WebAssembly (WASM)
```javascript
// Browser'da xavfsiz kod bajarish
const wasmModule = await WebAssembly.instantiate(codeBuffer);
const result = wasmModule.instance.exports.main();
```

## 🔍 Monitoring va Analytics

### Performance metrics:
- Execution time
- Memory usage
- Success rate
- Error patterns

### Security monitoring:
- Malicious code attempts
- Resource abuse
- System calls monitoring

## 🛠️ Troubleshooting

### Common issues:
1. **Compiler not found** - Install required compilers
2. **Permission denied** - Check file permissions
3. **Timeout errors** - Increase timeout limits
4. **Memory errors** - Optimize code or increase limits

### Debug mode:
```properties
logging.level.com.code.algonix.problems=DEBUG
```