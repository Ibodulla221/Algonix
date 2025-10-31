package com.code.algonix.problems;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_cases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String inputData;

    @Column(columnDefinition = "text")
    private String expectedOutput;

    private Integer timeLimitMs; // millisekundda vaqt limiti
}
