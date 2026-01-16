package com.code.algonix.contest;

import com.code.algonix.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "contest_participants", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"contest_id", "user_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContestParticipant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @Column(nullable = false)
    private LocalDateTime registeredAt;
    
    @Column(nullable = false)
    private Integer score = 0; // 100 ballik sistemada
    
    @Column(nullable = false)
    private Integer rank = 0;
    
    private Integer ratingChange = 0; // contest yakunida berilgan rating
    
    private Integer problemsSolved = 0;
    
    private Long totalPenalty = 0L; // vaqt penalti (sekundlarda)
}
