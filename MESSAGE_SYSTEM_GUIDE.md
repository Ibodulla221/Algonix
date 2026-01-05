# 📬 Algonix Message System Guide

## Umumiy ma'lumot

Algonix platformasida message/notification tizimi qo'shildi. Bu tizim foydalanuvchilarga turli xil xabarlar yuboradi:

- **Welcome message** - ro'yhatdan o'tganda
- **Problem solved** - masala yechganda
- **Level up** - level ko'tarilganda  
- **Daily streak** - har kun kirish uchun
- **Weekly streak** - haftalik faollik uchun
- **Monthly streak** - oylik faollik uchun
- **System messages** - tizim xabarlari

## 🎯 Asosiy funksiyalar

### 1. Message Types (Xabar turlari)

```java
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
```

### 2. Streak System (Ketma-ket kirish tizimi)

- **Daily Streak**: Har kun kirish uchun +5 coin
- **Weekly Streak**: 7, 14, 21... kun uchun +25 coin
- **Monthly Streak**: 30, 60, 90... kun uchun +100 coin

### 3. Message Ma'lumotlari

Har bir message quyidagi ma'lumotlarni o'z ichiga oladi:
- `title` - Xabar sarlavhasi
- `content` - Xabar matni
- `type` - Xabar turi
- `isRead` - O'qilgan/o'qilmagan
- `createdAt` - Yaratilgan vaqt
- `coinsEarned` - Olgan coinlar (agar reward bo'lsa)
- `xpEarned` - Olgan XP (agar reward bo'lsa)
- `newLevel` - Yangi level (agar level up bo'lsa)

## 🔧 API Endpoints

### Messages API

```http
# Barcha xabarlarni olish (pagination bilan)
GET /api/messages?page=0&size=20

# O'qilmagan xabarlar soni
GET /api/messages/unread-count

# O'qilmagan xabarlarni olish
GET /api/messages/unread

# Xabarni o'qilgan deb belgilash
PUT /api/messages/{messageId}/read

# Barcha xabarlarni o'qilgan deb belgilash
PUT /api/messages/read-all

# Streak tekshirish (test uchun)
POST /api/messages/check-streak

# Test system message yaratish
POST /api/messages/test-system-message
```

### User API

```http
# User profil ma'lumotlari (streak info bilan)
GET /api/user/profile

# User dashboard
GET /api/user/dashboard
```

## 🎮 Qanday ishlaydi?

### 1. Ro'yhatdan o'tish
```java
// AuthService.register() da
messageService.createWelcomeMessage(user);
```

### 2. Login qilish
```java
// AuthService.login() da
messageService.checkAndUpdateStreak(user);
```

### 3. Masala yechish
```java
// RewardService.processSuccessfulSubmission() da
messageService.createProblemSolvedMessage(user, problem, coins, xp, level, leveledUp);
```

### 4. Streak tekshirish
- Har login qilganda avtomatik tekshiriladi
- Ketma-ket kunlar hisoblanadi
- Mukofotlar beriladi va message yaratiladi

## 📊 Database Schema

### Messages Table
```sql
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    coins_earned INTEGER,
    xp_earned INTEGER,
    new_level INTEGER,
    problem_title VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### User Statistics (yangi fieldlar)
```sql
ALTER TABLE user_statistics 
ADD COLUMN last_login_date DATE,
ADD COLUMN current_streak INTEGER DEFAULT 0,
ADD COLUMN longest_streak INTEGER DEFAULT 0,
ADD COLUMN weekly_streak INTEGER DEFAULT 0,
ADD COLUMN monthly_streak INTEGER DEFAULT 0,
ADD COLUMN last_weekly_reward DATE,
ADD COLUMN last_monthly_reward DATE;
```

## 🧪 Test qilish

1. **Database migration ishga tushirish**:
```bash
mvn spring-boot:run
```

2. **Test HTTP requests**:
`test-messages.http` faylidan foydalaning

3. **Test scenario**:
   - Yangi user yarating
   - Login qiling
   - Masala yeching
   - Xabarlarni tekshiring

## 🎨 Frontend Integration

Frontend uchun quyidagi ma'lumotlar mavjud:

### Message Response
```typescript
interface MessageResponse {
  id: number;
  title: string;
  content: string;
  type: string;
  isRead: boolean;
  createdAt: string;
  readAt?: string;
  coinsEarned?: number;
  xpEarned?: number;
  newLevel?: number;
  problemTitle?: string;
}
```

### User Profile Response
```typescript
interface UserProfile {
  // Basic info
  id: number;
  username: string;
  email: string;
  
  // Game stats
  coins: number;
  level: number;
  experience: number;
  currentLevelXp: number;
  xpToNextLevel: number;
  
  // Streak info
  currentStreak: number;
  longestStreak: number;
  weeklyStreak: number;
  monthlyStreak: number;
  
  // Messages
  unreadMessages: number;
}
```

## 🚀 Keyingi qadamlar

1. **Real-time notifications** - WebSocket orqali
2. **Push notifications** - browser notifications
3. **Email notifications** - muhim xabarlar uchun
4. **Achievement system** - yangi yutuqlar
5. **Leaderboard notifications** - reyting o'zgarishlari

## 📝 Xulosa

Message tizimi to'liq ishga tushirildi va quyidagi imkoniyatlarni beradi:

✅ Welcome messages  
✅ Problem solving rewards  
✅ Level up notifications  
✅ Daily/Weekly/Monthly streaks  
✅ System messages  
✅ Message management (read/unread)  
✅ API endpoints  
✅ Database schema  
✅ Test scenarios  

Tizim tayyor va frontend bilan integratsiya qilish mumkin!