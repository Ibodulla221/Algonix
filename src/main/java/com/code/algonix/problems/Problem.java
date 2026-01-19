package com.code.algonix.problems;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "problems")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String slug; // "two-sum"

    @Column(nullable = false)
    private String title; // "Two Sum"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty; // EASY, MEDIUM, HARD

    @ElementCollection
    @CollectionTable(name = "problem_categories", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "category")
    private List<String> categories = new ArrayList<>(); // ["array", "hash-table"]

    @ElementCollection
    @CollectionTable(name = "problem_tags", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String descriptionHtml;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "problem")
    private List<ProblemExample> examples = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "problem_constraints", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "constraint_text", columnDefinition = "TEXT")
    private List<String> constraints = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "problem_hints", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "hint", columnDefinition = "TEXT")
    private List<String> hints = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "problem")
    private List<CodeTemplate> codeTemplates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "problem")
    private List<TestCase> testCases = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "problem_related", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "related_problem_id")
    private List<Long> relatedProblems = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "problem_companies", joinColumns = @JoinColumn(name = "problem_id"))
    @Column(name = "company")
    private List<String> companies = new ArrayList<>();

    private Integer likes = 0;
    private Integer dislikes = 0;
    private Double acceptanceRate = 0.0;
    private Long totalSubmissions = 0L;
    private Long totalAccepted = 0L;
    private Double frequency = 0.0; // 0.0 - 1.0
    private Boolean isPremium = false;
    
    @Builder.Default
    private Integer timeLimitMs = 2000; // Time limit in milliseconds (default 2000ms)
    
    @Builder.Default
    private Integer memoryLimitMb = 512; // Memory limit in MB (default 512MB)
    
    private Integer globalSequenceNumber; // Global sequence number for ordering
    
    // Contest-specific fields
    private Boolean isContestOnly = false; // Masala faqat contest uchunmi
    // contestId ni olib tashladik - contest yaratilgandan keyin bog'lanadi
    private LocalDateTime publishTime; // Qachon umumiy foydalanuvchilarga ko'rsatiladi

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum Difficulty {
        BEGINNER, BASIC, EASY, NORMAL, MEDIUM, HARD
    }
}
