package com.code.algonix.contest;

import com.code.algonix.problems.Problem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contest_problems")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContestProblem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;
    
    @Column(nullable = false)
    private String symbol; // A, B, C, D...
    
    @Column(nullable = false)
    private Integer points; // ball
    
    @Column(nullable = false)
    private Integer orderIndex;
    
    private Integer attemptsCount = 0;
    private Integer solvedCount = 0;
    private Integer unsolvedCount = 0;
    private Integer attemptUsersCount = 0;
    private Double delta; // rating delta
}
