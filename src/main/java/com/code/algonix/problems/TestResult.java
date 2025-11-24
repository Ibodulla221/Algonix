package com.code.algonix.problems;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @ManyToOne
    @JoinColumn(name = "test_case_id")
    private TestCase testCase;

    @Enumerated(EnumType.STRING)
    private TestStatus status;

    private Integer runtime; // milliseconds
    private Double memory; // MB

    @Column(columnDefinition = "TEXT")
    private String input;

    @Column(columnDefinition = "TEXT")
    private String expectedOutput;

    @Column(columnDefinition = "TEXT")
    private String actualOutput;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    public enum TestStatus {
        PASSED,
        FAILED,
        TIME_LIMIT_EXCEEDED,
        RUNTIME_ERROR
    }
}
