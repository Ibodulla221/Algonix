package com.code.algonix.contest;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String number;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String imageUrl;
    
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    @Column(nullable = false)
    private Integer durationSeconds;
    
    @Column(nullable = false)
    private Integer problemCount = 0;
    
    @Column(nullable = false)
    private Integer participantsCount = 0;
    
    @Column(columnDefinition = "TEXT")
    private String prizePool; // JSON string
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContestStatus status = ContestStatus.UPCOMING;
    
    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContestProblem> problems = new ArrayList<>();
    
    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContestParticipant> participants = new ArrayList<>();
    
    public enum ContestStatus {
        UPCOMING, ACTIVE, FINISHED, REGISTERED
    }
}
