package com.code.algonix.messages;

import java.time.LocalDateTime;

import com.code.algonix.user.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;
    
    @Builder.Default
    private Boolean isRead = false;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime readAt;
    
    // Reward ma'lumotlari (agar reward message bo'lsa)
    private Integer coinsEarned;
    private Integer xpEarned;
    private Integer newLevel;
    private String problemTitle;
    
    public enum MessageType {
        WELCOME,           // Ro'yhatdan o'tganda
        PROBLEM_SOLVED,    // Masala yechganda
        LEVEL_UP,          // Level ko'tarilganda
        DAILY_STREAK,      // Kunlik streak
        WEEKLY_STREAK,     // Haftalik streak
        MONTHLY_STREAK,    // Oylik streak
        ACHIEVEMENT,       // Yutuq
        SYSTEM,           // Tizim xabari
        REWARD             // Mukofot
    }
}