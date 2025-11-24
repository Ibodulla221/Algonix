package com.code.algonix.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;

    private Integer totalSolved = 0;
    private Integer easySolved = 0;
    private Integer mediumSolved = 0;
    private Integer hardSolved = 0;
    private Double acceptanceRate = 0.0;
    private Integer ranking = 0;
    private Integer reputation = 0;
    private Integer streakDays = 0; // ketma-ket kun
}
