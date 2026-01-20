package com.code.algonix.problems;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sun.management.OperatingSystemMXBean;

import lombok.extern.slf4j.Slf4j;

/**
 * Tizim resurslarini monitoring qilish servisi
 */
@Service
@Slf4j
public class ResourceMonitoringService {

    @Value("${code.execution.memory-limit:512}")
    private int memoryLimitMB;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    /**
     * Jarayonni monitoring qilishni boshlash
     */
    public void startMonitoring(Process process, long timeoutMs) {
        // Memory monitoring
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (process.isAlive()) {
                    long usedMemory = memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024); // MB
                    if (usedMemory > memoryLimitMB) {
                        log.warn("Memory limit exceeded: {} MB > {} MB", usedMemory, memoryLimitMB);
                        process.destroyForcibly();
                    }
                }
            } catch (Exception e) {
                log.debug("Memory monitoring error", e);
            }
        }, 100, 100, TimeUnit.MILLISECONDS);

        // CPU monitoring
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (process.isAlive()) {
                    double cpuUsage = osBean.getProcessCpuLoad() * 100;
                    if (cpuUsage > 90.0) { // 90% dan yuqori
                        log.warn("High CPU usage detected: {}%", cpuUsage);
                    }
                }
            } catch (Exception e) {
                log.debug("CPU monitoring error", e);
            }
        }, 500, 500, TimeUnit.MILLISECONDS);

        // Timeout monitoring
        scheduler.schedule(() -> {
            if (process.isAlive()) {
                log.warn("Process timeout reached, terminating");
                process.destroyForcibly();
            }
        }, timeoutMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Monitoring'ni to'xtatish
     */
    public void stopMonitoring() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Tizim resurslarini tekshirish
     */
    public boolean hasEnoughResources() {
        try {
            // Available memory check - kamroq talab qilamiz
            long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024); // MB
            log.debug("Free memory: {} MB, required: {} MB", freeMemory, memoryLimitMB);
            
            if (freeMemory < memoryLimitMB) { // Faqat 1x memory bo'lishi yetarli
                log.warn("Insufficient memory: {} MB free, need {} MB", freeMemory, memoryLimitMB);
                return false;
            }

            // CPU load check - Windows'da systemLoad -1 bo'lishi mumkin
            double systemLoad = osBean.getSystemLoadAverage();
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            log.debug("System load: {}, processors: {}", systemLoad, availableProcessors);
            
            if (systemLoad > 0 && systemLoad > availableProcessors * 1.5) { // 150% dan yuqori load
                log.warn("High system load: {} (processors: {})", systemLoad, availableProcessors);
                return false;
            }

            log.debug("Resources check passed: memory={} MB, load={}", freeMemory, systemLoad);
            return true;
        } catch (Exception e) {
            log.warn("Resource check failed", e);
            return true; // Xato bo'lsa, bajarishga ruxsat beramiz
        }
    }

    /**
     * Tizim statistikasini olish
     */
    public SystemStats getSystemStats() {
        try {
            long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
            long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
            long usedMemory = totalMemory - freeMemory;
            
            double cpuUsage = osBean.getProcessCpuLoad() * 100;
            double systemLoad = osBean.getSystemLoadAverage();
            int processors = Runtime.getRuntime().availableProcessors();

            return SystemStats.builder()
                    .totalMemoryMB(totalMemory)
                    .usedMemoryMB(usedMemory)
                    .freeMemoryMB(freeMemory)
                    .cpuUsagePercent(cpuUsage)
                    .systemLoad(systemLoad)
                    .availableProcessors(processors)
                    .build();
        } catch (Exception e) {
            log.warn("Failed to get system stats", e);
            return SystemStats.builder().build();
        }
    }

    /**
     * Tizim statistikasi uchun data class
     */
    public static class SystemStats {
        private final long totalMemoryMB;
        private final long usedMemoryMB;
        private final long freeMemoryMB;
        private final double cpuUsagePercent;
        private final double systemLoad;
        private final int availableProcessors;

        private SystemStats(Builder builder) {
            this.totalMemoryMB = builder.totalMemoryMB;
            this.usedMemoryMB = builder.usedMemoryMB;
            this.freeMemoryMB = builder.freeMemoryMB;
            this.cpuUsagePercent = builder.cpuUsagePercent;
            this.systemLoad = builder.systemLoad;
            this.availableProcessors = builder.availableProcessors;
        }

        public static Builder builder() {
            return new Builder();
        }

        // Getters
        public long getTotalMemoryMB() { return totalMemoryMB; }
        public long getUsedMemoryMB() { return usedMemoryMB; }
        public long getFreeMemoryMB() { return freeMemoryMB; }
        public double getCpuUsagePercent() { return cpuUsagePercent; }
        public double getSystemLoad() { return systemLoad; }
        public int getAvailableProcessors() { return availableProcessors; }

        public static class Builder {
            private long totalMemoryMB;
            private long usedMemoryMB;
            private long freeMemoryMB;
            private double cpuUsagePercent;
            private double systemLoad;
            private int availableProcessors;

            public Builder totalMemoryMB(long totalMemoryMB) {
                this.totalMemoryMB = totalMemoryMB;
                return this;
            }

            public Builder usedMemoryMB(long usedMemoryMB) {
                this.usedMemoryMB = usedMemoryMB;
                return this;
            }

            public Builder freeMemoryMB(long freeMemoryMB) {
                this.freeMemoryMB = freeMemoryMB;
                return this;
            }

            public Builder cpuUsagePercent(double cpuUsagePercent) {
                this.cpuUsagePercent = cpuUsagePercent;
                return this;
            }

            public Builder systemLoad(double systemLoad) {
                this.systemLoad = systemLoad;
                return this;
            }

            public Builder availableProcessors(int availableProcessors) {
                this.availableProcessors = availableProcessors;
                return this;
            }

            public SystemStats build() {
                return new SystemStats(this);
            }
        }

        @Override
        public String toString() {
            return String.format(
                "SystemStats{memory=%d/%d MB (%.1f%%), CPU=%.1f%%, load=%.2f, processors=%d}",
                usedMemoryMB, totalMemoryMB, (usedMemoryMB * 100.0 / totalMemoryMB),
                cpuUsagePercent, systemLoad, availableProcessors
            );
        }
    }
}