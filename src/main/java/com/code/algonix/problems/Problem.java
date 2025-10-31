package com.code.algonix.problems;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "problems")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "text")
    private String description;

    // simple: store expected outputs / inputs as JSON string or separate entity
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "problem_id")
    private List<TestCase> testCases;
}
