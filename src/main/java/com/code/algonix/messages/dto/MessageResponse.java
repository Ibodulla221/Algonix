package com.code.algonix.messages.dto;

import com.code.algonix.messages.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private String title;
    private String content;
    private Message.MessageType type;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    
    // Reward ma'lumotlari
    private Integer coinsEarned;
    private Integer xpEarned;
    private Integer newLevel;
    private String problemTitle;
    
    public static MessageResponse fromEntity(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .title(message.getTitle())
                .content(message.getContent())
                .type(message.getType())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .readAt(message.getReadAt())
                .coinsEarned(message.getCoinsEarned())
                .xpEarned(message.getXpEarned())
                .newLevel(message.getNewLevel())
                .problemTitle(message.getProblemTitle())
                .build();
    }
}