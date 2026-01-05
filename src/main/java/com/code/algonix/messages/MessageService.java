package com.code.algonix.messages;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code.algonix.messages.dto.MessageResponse;
import com.code.algonix.problems.Problem;
import com.code.algonix.user.UserEntity;
import com.code.algonix.user.UserStatistics;
import com.code.algonix.user.UserStatisticsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final UserStatisticsRepository userStatisticsRepository;
    
    // Streak reward constants
    private static final int DAILY_STREAK_COINS = 5;
    private static final int WEEKLY_STREAK_COINS = 25;
    private static final int MONTHLY_STREAK_COINS = 100;
    
    /**
     * Foydalanuvchi ro'yhatdan o'tganda welcome message yaratish
     */
    @Transactional
    public void createWelcomeMessage(UserEntity user) {
        Message message = Message.builder()
                .user(user)
                .type(Message.MessageType.WELCOME)
                .title("🎉 Algonix-ga xush kelibsiz!")
                .content("Salom " + user.getUsername() + "! Algonix platformasiga xush kelibsiz. " +
                        "Bu yerda siz dasturlash masalalarini yechib, o'z bilimingizni oshirishingiz mumkin. " +
                        "Har bir yechilgan masala uchun coin va XP olasiz. Omad tilaymiz! 🚀")
                .build();
        
        messageRepository.save(message);
        log.info("Welcome message created for user: {}", user.getUsername());
    }
    
    /**
     * Masala yechilganda reward message yaratish
     */
    @Transactional
    public void createProblemSolvedMessage(UserEntity user, Problem problem, 
                                         int coinsEarned, int xpEarned, int newLevel, boolean leveledUp) {
        String title = leveledUp ? 
                "🎊 Masala yechildi va Level ko'tarildi!" : 
                "✅ Masala muvaffaqiyatli yechildi!";
        
        StringBuilder content = new StringBuilder();
        content.append("Tabriklaymiz! \"").append(problem.getTitle()).append("\" masalasini yechdingiz!\n\n");
        content.append("🪙 Olgan coinlaringiz: ").append(coinsEarned).append("\n");
        content.append("⭐ Olgan XP: ").append(xpEarned).append("\n");
        
        if (leveledUp) {
            content.append("🆙 Yangi level: ").append(newLevel).append("\n");
            content.append("\nSiz yangi levelga ko'tarildingiz! Davom eting! 💪");
        }
        
        Message message = Message.builder()
                .user(user)
                .type(leveledUp ? Message.MessageType.LEVEL_UP : Message.MessageType.PROBLEM_SOLVED)
                .title(title)
                .content(content.toString())
                .coinsEarned(coinsEarned)
                .xpEarned(xpEarned)
                .newLevel(newLevel)
                .problemTitle(problem.getTitle())
                .build();
        
        messageRepository.save(message);
        log.info("Problem solved message created for user: {}, problem: {}", user.getUsername(), problem.getTitle());
    }
    
    /**
     * Foydalanuvchi login qilganda streak tekshirish va message yaratish
     */
    @Transactional
    public void checkAndUpdateStreak(UserEntity user) {
        UserStatistics stats = user.getStatistics();
        if (stats == null) {
            stats = UserStatistics.builder()
                    .user(user)
                    .coins(0)
                    .experience(0)
                    .level(1)
                    .currentLevelXp(0)
                    .currentStreak(0)
                    .longestStreak(0)
                    .weeklyStreak(0)
                    .monthlyStreak(0)
                    .build();
            user.setStatistics(stats);
        }
        
        LocalDate today = LocalDate.now();
        LocalDate lastLogin = stats.getLastLoginDate();
        
        if (lastLogin == null) {
            // Birinchi marta kirish
            stats.setLastLoginDate(today);
            stats.setCurrentStreak(1);
            stats.setLongestStreak(1);
            userStatisticsRepository.save(stats);
            return;
        }
        
        long daysBetween = ChronoUnit.DAYS.between(lastLogin, today);
        
        if (daysBetween == 0) {
            // Bugun allaqachon kirgan
            return;
        } else if (daysBetween == 1) {
            // Ketma-ket kun
            stats.setCurrentStreak(stats.getCurrentStreak() + 1);
            stats.setLastLoginDate(today);
            
            // Longest streak yangilash
            if (stats.getCurrentStreak() > stats.getLongestStreak()) {
                stats.setLongestStreak(stats.getCurrentStreak());
            }
            
            // Daily streak reward
            createDailyStreakMessage(user, stats.getCurrentStreak());
            stats.setCoins(stats.getCoins() + DAILY_STREAK_COINS);
            
            // Weekly streak check (7, 14, 21, ... kun)
            if (stats.getCurrentStreak() % 7 == 0) {
                createWeeklyStreakMessage(user, stats.getCurrentStreak() / 7);
                stats.setCoins(stats.getCoins() + WEEKLY_STREAK_COINS);
                stats.setWeeklyStreak(stats.getWeeklyStreak() + 1);
                stats.setLastWeeklyReward(today);
            }
            
            // Monthly streak check (30, 60, 90, ... kun)
            if (stats.getCurrentStreak() % 30 == 0) {
                createMonthlyStreakMessage(user, stats.getCurrentStreak() / 30);
                stats.setCoins(stats.getCoins() + MONTHLY_STREAK_COINS);
                stats.setMonthlyStreak(stats.getMonthlyStreak() + 1);
                stats.setLastMonthlyReward(today);
            }
            
        } else {
            // Streak uzildi
            if (stats.getCurrentStreak() > 0) {
                createStreakBrokenMessage(user, stats.getCurrentStreak());
            }
            stats.setCurrentStreak(1);
            stats.setLastLoginDate(today);
        }
        
        userStatisticsRepository.save(stats);
        log.info("Streak updated for user: {}, current streak: {}", user.getUsername(), stats.getCurrentStreak());
    }
    
    private void createDailyStreakMessage(UserEntity user, int streakDays) {
        String title = "🔥 Kunlik faollik!";
        String content = String.format("Ajoyib! Siz %d kun ketma-ket platformaga kirdingiz!\n\n" +
                "🪙 Mukofot: %d coin\n\n" +
                "Davom eting va yanada ko'p mukofotlar oling! 💪", 
                streakDays, DAILY_STREAK_COINS);
        
        Message message = Message.builder()
                .user(user)
                .type(Message.MessageType.DAILY_STREAK)
                .title(title)
                .content(content)
                .coinsEarned(DAILY_STREAK_COINS)
                .build();
        
        messageRepository.save(message);
    }
    
    private void createWeeklyStreakMessage(UserEntity user, int weekCount) {
        String title = "🏆 Haftalik yutuq!";
        String content = String.format("Zo'r! Siz %d hafta ketma-ket faol bo'ldingiz!\n\n" +
                "🪙 Maxsus mukofot: %d coin\n\n" +
                "Sizning qat'iyatingiz hayratlanarli! 🌟", 
                weekCount, WEEKLY_STREAK_COINS);
        
        Message message = Message.builder()
                .user(user)
                .type(Message.MessageType.WEEKLY_STREAK)
                .title(title)
                .content(content)
                .coinsEarned(WEEKLY_STREAK_COINS)
                .build();
        
        messageRepository.save(message);
    }
    
    private void createMonthlyStreakMessage(UserEntity user, int monthCount) {
        String title = "👑 Oylik chempion!";
        String content = String.format("Ajoyib! Siz %d oy ketma-ket faol bo'ldingiz!\n\n" +
                "🪙 Katta mukofot: %d coin\n\n" +
                "Siz haqiqiy dasturlash ustasisiz! Davom eting! 🚀", 
                monthCount, MONTHLY_STREAK_COINS);
        
        Message message = Message.builder()
                .user(user)
                .type(Message.MessageType.MONTHLY_STREAK)
                .title(title)
                .content(content)
                .coinsEarned(MONTHLY_STREAK_COINS)
                .build();
        
        messageRepository.save(message);
    }
    
    private void createStreakBrokenMessage(UserEntity user, int lostStreak) {
        String title = "😔 Streak uzildi";
        String content = String.format("Afsuski, %d kunlik streak uzildi.\n\n" +
                "Xafa bo'lmang! Yangi streak boshlang va yanada yuqori natijaga erishing! 💪\n\n" +
                "Har qanday muvaffaqiyat kichik qadamlardan boshlanadi. 🌟", lostStreak);
        
        Message message = Message.builder()
                .user(user)
                .type(Message.MessageType.SYSTEM)
                .title(title)
                .content(content)
                .build();
        
        messageRepository.save(message);
    }
    
    /**
     * Foydalanuvchining barcha xabarlarini olish
     */
    public Page<MessageResponse> getUserMessages(UserEntity user, Pageable pageable) {
        Page<Message> messages = messageRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return messages.map(MessageResponse::fromEntity);
    }
    
    /**
     * O'qilmagan xabarlar soni
     */
    public long getUnreadCount(UserEntity user) {
        return messageRepository.countByUserAndIsReadFalse(user);
    }
    
    /**
     * O'qilmagan xabarlarni olish
     */
    public List<MessageResponse> getUnreadMessages(UserEntity user) {
        List<Message> messages = messageRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
        return messages.stream()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Xabarni o'qilgan deb belgilash
     */
    @Transactional
    public boolean markAsRead(UserEntity user, Long messageId) {
        int updated = messageRepository.markAsRead(messageId, user);
        return updated > 0;
    }
    
    /**
     * Barcha xabarlarni o'qilgan deb belgilash
     */
    @Transactional
    public int markAllAsRead(UserEntity user) {
        return messageRepository.markAllAsRead(user);
    }
    
    /**
     * System message yaratish
     */
    @Transactional
    public void createSystemMessage(UserEntity user, String title, String content) {
        Message message = Message.builder()
                .user(user)
                .type(Message.MessageType.SYSTEM)
                .title(title)
                .content(content)
                .build();
        
        messageRepository.save(message);
        log.info("System message created for user: {}", user.getUsername());
    }
}