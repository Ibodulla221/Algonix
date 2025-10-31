package com.code.algonix.problems;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long problemId;
    private String language;

    @Column(columnDefinition = "text")
    private String code;

    private String status; // PENDING, RUNNING, PASSED, FAILED, ERROR
    @Column(columnDefinition = "text")
    private String resultJson; // per-test results, error messages etc.

    private Instant createdAt;
}
