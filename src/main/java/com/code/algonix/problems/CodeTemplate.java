package com.code.algonix.problems;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "code_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(nullable = false)
    private String language; // python, java, javascript, cpp

    @Column(columnDefinition = "TEXT", nullable = false)
    private String code;
}
