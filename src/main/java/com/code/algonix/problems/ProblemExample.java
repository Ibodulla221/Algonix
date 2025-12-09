package com.code.algonix.problems;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problem_examples")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemExample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(columnDefinition = "TEXT")
    private String input;

    @Column(columnDefinition = "TEXT")
    private String target;

    @Column(columnDefinition = "TEXT")
    private String output;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    private Integer orderIndex;
}
