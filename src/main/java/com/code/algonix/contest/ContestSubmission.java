package com.code.algonix.contest;

import com.code.algonix.problems.Submission;
import com.code.algonix.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "contest_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContestSubmission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_problem_id", nullable = false)
    private ContestProblem contestProblem;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;
    
    @Column(nullable = false)
    private LocalDateTime submittedAt;
    
    @Column(nullable = false)
    private Boolean isAccepted;
    
    private Integer score; // bu submission uchun olingan ball
    
    private Long timeTaken; // contest boshlanganidan beri o'tgan vaqt (sekundlarda)
}
