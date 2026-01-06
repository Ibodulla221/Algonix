package com.code.algonix.admin.dto;

import com.code.algonix.problems.Submission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSubmissionResponse {
    private Long id;
    private String username;
    private String problemTitle;
    private String language;
    private Submission.SubmissionStatus status;
    private LocalDateTime submittedAt;
    private Integer runtime;
    private Double memory;
}