package com.code.algonix.messages;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.code.algonix.messages.dto.MessageResponse;
import com.code.algonix.user.UserEntity;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {
    
    private final MessageService messageService;
    
    /**
     * Foydalanuvchining barcha xabarlarini olish (pagination bilan)
     */
    @GetMapping
    public ResponseEntity<Page<MessageResponse>> getMessages(
            @AuthenticationPrincipal UserEntity user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageResponse> messages = messageService.getUserMessages(user, pageable);
        return ResponseEntity.ok(messages);
    }
    
    /**
     * O'qilmagan xabarlar soni
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal UserEntity user) {
        long count = messageService.getUnreadCount(user);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }
    
    /**
     * O'qilmagan xabarlarni olish
     */
    @GetMapping("/unread")
    public ResponseEntity<List<MessageResponse>> getUnreadMessages(@AuthenticationPrincipal UserEntity user) {
        List<MessageResponse> messages = messageService.getUnreadMessages(user);
        return ResponseEntity.ok(messages);
    }
    
    /**
     * Xabarni o'qilgan deb belgilash
     */
    @PutMapping("/{messageId}/read")
    public ResponseEntity<Map<String, String>> markAsRead(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long messageId) {
        
        boolean success = messageService.markAsRead(user, messageId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Xabar o'qilgan deb belgilandi"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Barcha xabarlarni o'qilgan deb belgilash
     */
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@AuthenticationPrincipal UserEntity user) {
        int updatedCount = messageService.markAllAsRead(user);
        return ResponseEntity.ok(Map.of(
                "message", "Barcha xabarlar o'qilgan deb belgilandi",
                "updatedCount", updatedCount
        ));
    }
    
    /**
     * Streak ma'lumotlarini yangilash (manual test uchun)
     */
    @PostMapping("/check-streak")
    public ResponseEntity<Map<String, String>> checkStreak(@AuthenticationPrincipal UserEntity user) {
        messageService.checkAndUpdateStreak(user);
        return ResponseEntity.ok(Map.of("message", "Streak tekshirildi va yangilandi"));
    }
    
    /**
     * Test uchun system message yaratish
     */
    @PostMapping("/test-system-message")
    public ResponseEntity<Map<String, String>> createTestSystemMessage(@AuthenticationPrincipal UserEntity user) {
        messageService.createSystemMessage(user, 
                "🧪 Test xabari", 
                "Bu test uchun yaratilgan system message. Message tizimi to'g'ri ishlayapti!");
        return ResponseEntity.ok(Map.of("message", "Test system message yaratildi"));
    }
}