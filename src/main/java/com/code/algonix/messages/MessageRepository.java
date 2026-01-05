package com.code.algonix.messages;

import com.code.algonix.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Foydalanuvchining barcha xabarlarini olish
    Page<Message> findByUserOrderByCreatedAtDesc(UserEntity user, Pageable pageable);
    
    // O'qilmagan xabarlar soni
    long countByUserAndIsReadFalse(UserEntity user);
    
    // O'qilmagan xabarlarni olish
    List<Message> findByUserAndIsReadFalseOrderByCreatedAtDesc(UserEntity user);
    
    // Xabarni o'qilgan deb belgilash
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true, m.readAt = CURRENT_TIMESTAMP WHERE m.id = :messageId AND m.user = :user")
    int markAsRead(@Param("messageId") Long messageId, @Param("user") UserEntity user);
    
    // Barcha xabarlarni o'qilgan deb belgilash
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true, m.readAt = CURRENT_TIMESTAMP WHERE m.user = :user AND m.isRead = false")
    int markAllAsRead(@Param("user") UserEntity user);
    
    // Ma'lum turdagi xabarlarni olish
    List<Message> findByUserAndTypeOrderByCreatedAtDesc(UserEntity user, Message.MessageType type);
}