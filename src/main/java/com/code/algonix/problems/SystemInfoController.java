package com.code.algonix.problems;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
@Tag(name = "System Info", description = "Tizim ma'lumotlari API")
public class SystemInfoController {

    private final ResourceMonitoringService resourceMonitoringService;
    private final CodeExecutionServiceSelector executionServiceSelector;

    @GetMapping("/stats")
    @Operation(summary = "Tizim statistikasini olish")
    public ResponseEntity<SystemInfoResponse> getSystemStats() {
        ResourceMonitoringService.SystemStats stats = resourceMonitoringService.getSystemStats();
        String executionMethod = executionServiceSelector.getExecutionMethod();
        
        SystemInfoResponse response = SystemInfoResponse.builder()
                .totalMemoryMB(stats.getTotalMemoryMB())
                .usedMemoryMB(stats.getUsedMemoryMB())
                .freeMemoryMB(stats.getFreeMemoryMB())
                .cpuUsagePercent(stats.getCpuUsagePercent())
                .systemLoad(stats.getSystemLoad())
                .availableProcessors(stats.getAvailableProcessors())
                .executionMethod(executionMethod)
                .hasEnoughResources(resourceMonitoringService.hasEnoughResources())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Tizim sog'ligini tekshirish")
    public ResponseEntity<HealthResponse> getSystemHealth() {
        boolean healthy = resourceMonitoringService.hasEnoughResources();
        String status = healthy ? "HEALTHY" : "UNHEALTHY";
        String message = healthy ? "Tizim normal ishlayapti" : "Tizim resurslari yetarli emas";
        
        HealthResponse response = HealthResponse.builder()
                .status(status)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
        
        return ResponseEntity.ok(response);
    }

    // Response DTOs
    public static class SystemInfoResponse {
        private final long totalMemoryMB;
        private final long usedMemoryMB;
        private final long freeMemoryMB;
        private final double cpuUsagePercent;
        private final double systemLoad;
        private final int availableProcessors;
        private final String executionMethod;
        private final boolean hasEnoughResources;

        private SystemInfoResponse(Builder builder) {
            this.totalMemoryMB = builder.totalMemoryMB;
            this.usedMemoryMB = builder.usedMemoryMB;
            this.freeMemoryMB = builder.freeMemoryMB;
            this.cpuUsagePercent = builder.cpuUsagePercent;
            this.systemLoad = builder.systemLoad;
            this.availableProcessors = builder.availableProcessors;
            this.executionMethod = builder.executionMethod;
            this.hasEnoughResources = builder.hasEnoughResources;
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
        public String getExecutionMethod() { return executionMethod; }
        public boolean isHasEnoughResources() { return hasEnoughResources; }

        public static class Builder {
            private long totalMemoryMB;
            private long usedMemoryMB;
            private long freeMemoryMB;
            private double cpuUsagePercent;
            private double systemLoad;
            private int availableProcessors;
            private String executionMethod;
            private boolean hasEnoughResources;

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

            public Builder executionMethod(String executionMethod) {
                this.executionMethod = executionMethod;
                return this;
            }

            public Builder hasEnoughResources(boolean hasEnoughResources) {
                this.hasEnoughResources = hasEnoughResources;
                return this;
            }

            public SystemInfoResponse build() {
                return new SystemInfoResponse(this);
            }
        }
    }

    public static class HealthResponse {
        private final String status;
        private final String message;
        private final long timestamp;

        private HealthResponse(Builder builder) {
            this.status = builder.status;
            this.message = builder.message;
            this.timestamp = builder.timestamp;
        }

        public static Builder builder() {
            return new Builder();
        }

        // Getters
        public String getStatus() { return status; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }

        public static class Builder {
            private String status;
            private String message;
            private long timestamp;

            public Builder status(String status) {
                this.status = status;
                return this;
            }

            public Builder message(String message) {
                this.message = message;
                return this;
            }

            public Builder timestamp(long timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            public HealthResponse build() {
                return new HealthResponse(this);
            }
        }
    }
}