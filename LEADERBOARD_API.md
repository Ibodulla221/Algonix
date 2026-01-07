# Leaderboard API Documentation

Foydalanuvchilar reytingi (leaderboard) uchun API'lar.

## Endpoints

### 1. GET /api/leaderboard

Foydalanuvchilar reytingi ro'yxatini olish.

#### URL
```
GET /api/leaderboard
```

#### Authentication
- **Required**: No (Public API)

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `page` | Integer | No | 0 | Sahifa raqami (0-dan boshlanadi) |
| `size` | Integer | No | 20 | Sahifadagi foydalanuvchilar soni |
| `sortBy` | String | No | "totalSolved" | Saralash turi |
| `search` | String | No | - | Username bo'yicha qidiruv |

#### Sort Options

| Value | Description |
|-------|-------------|
| `totalSolved` | Yechilgan masalalar soni bo'yicha |
| `experience` | Tajriba ballari bo'yicha |
| `level` | Level bo'yicha |
| `currentStreak` | Joriy streak bo'yicha |
| `longestStreak` | Eng uzun streak bo'yicha |

#### Request Examples

##### Oddiy leaderboard
```http
GET /api/leaderboard?page=0&size=10
```

##### Experience bo'yicha saralash
```http
GET /api/leaderboard?sortBy=experience&page=0&size=10
```

##### Username bo'yicha qidiruv
```http
GET /api/leaderboard?search=john&page=0&size=10
```

#### Response Format

```json
{
  "users": [
    {
      "userId": 1,
      "username": "AliValiyev",
      "email": "ali@gmail.com",
      "location": "O'zbekiston",
      "ranking": 1,
      "totalSolved": 120,
      "beginnerSolved": 25,
      "basicSolved": 30,
      "normalSolved": 35,
      "mediumSolved": 25,
      "hardSolved": 5,
      "acceptanceRate": 85.5,
      "experience": 2450,
      "level": 8,
      "currentLevelXp": 45,
      "coins": 1200,
      "currentStreak": 15,
      "longestStreak": 25,
      "lastLoginDate": "2024-01-07",
      "status": "online",
      "badge": "Expert"
    }
  ],
  "total": 150,
  "page": 0,
  "pageSize": 10,
  "sortBy": "totalSolved"
}
```

### 2. GET /api/leaderboard/top/{count}

Top N foydalanuvchilarni olish.

#### URL
```
GET /api/leaderboard/top/{count}
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `count` | Integer | Yes | Top foydalanuvchilar soni (max: 100) |

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `sortBy` | String | No | "totalSolved" | Saralash turi |

#### Request Examples

```http
GET /api/leaderboard/top/10
GET /api/leaderboard/top/5?sortBy=experience
```

#### Response Format

```json
{
  "users": [
    {
      "userId": 1,
      "username": "AliValiyev",
      "ranking": 1,
      "totalSolved": 120,
      "experience": 2450,
      "level": 8,
      "status": "online",
      "badge": "Expert"
    }
  ],
  "total": 5,
  "page": 0,
  "pageSize": 5,
  "sortBy": "totalSolved"
}
```

### 3. GET /api/leaderboard/user/{userId}/ranking

Belgilangan foydalanuvchining reytingini olish.

#### URL
```
GET /api/leaderboard/user/{userId}/ranking
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `userId` | Long | Yes | Foydalanuvchi ID'si |

#### Request Examples

```http
GET /api/leaderboard/user/123/ranking
```

#### Response Format

```json
{
  "userId": 123,
  "username": "JohnDoe",
  "email": "john@example.com",
  "location": "O'zbekiston",
  "ranking": 45,
  "totalSolved": 67,
  "beginnerSolved": 15,
  "basicSolved": 20,
  "normalSolved": 20,
  "mediumSolved": 10,
  "hardSolved": 2,
  "acceptanceRate": 78.5,
  "experience": 1340,
  "level": 6,
  "currentLevelXp": 23,
  "coins": 670,
  "currentStreak": 5,
  "longestStreak": 12,
  "lastLoginDate": "2024-01-06",
  "status": "away",
  "badge": "Advanced"
}
```

## Response Fields

### User Ranking Object

| Field | Type | Description |
|-------|------|-------------|
| `userId` | Long | Foydalanuvchi ID'si |
| `username` | String | Foydalanuvchi nomi |
| `email` | String | Email manzili |
| `location` | String | Joylashuv (mamlakat/shahar) |
| `ranking` | Integer | Global reyting pozitsiyasi |
| `totalSolved` | Integer | Jami yechilgan masalalar |
| `beginnerSolved` | Integer | Beginner masalalar soni |
| `basicSolved` | Integer | Basic masalalar soni |
| `normalSolved` | Integer | Normal masalalar soni |
| `mediumSolved` | Integer | Medium masalalar soni |
| `hardSolved` | Integer | Hard masalalar soni |
| `acceptanceRate` | Double | Qabul qilish foizi |
| `experience` | Integer | Tajriba ballari |
| `level` | Integer | Foydalanuvchi darajasi |
| `currentLevelXp` | Integer | Joriy darajadagi XP |
| `coins` | Integer | Coin balansi |
| `currentStreak` | Integer | Joriy streak (kun) |
| `longestStreak` | Integer | Eng uzun streak |
| `lastLoginDate` | Date | Oxirgi kirgan sana |
| `status` | String | Holat: "online", "away", "offline" |
| `badge` | String | Badge: "Beginner", "Intermediate", "Advanced", "Expert", "Master" |

### Status Values

| Status | Description |
|--------|-------------|
| `online` | Bugun kirgan |
| `away` | 1-3 kun oldin kirgan |
| `offline` | 3+ kun oldin kirgan |

### Badge Levels

| Badge | Requirements |
|-------|-------------|
| `Beginner` | < 10 masala yoki < 3 level |
| `Intermediate` | 10+ masala yoki 3+ level |
| `Advanced` | 25+ masala yoki 5+ level |
| `Expert` | 50+ masala yoki 7+ level |
| `Master` | 100+ masala yoki 10+ level |

## Error Responses

### 404 Not Found
```json
{
  "error": "User statistics not found"
}
```

## Usage Examples

### Frontend Integration

```javascript
// Leaderboard ro'yxatini olish
fetch('/api/leaderboard?page=0&size=20&sortBy=totalSolved')
  .then(response => response.json())
  .then(data => {
    console.log('Leaderboard:', data.users);
    console.log('Total users:', data.total);
  });

// Top 10 foydalanuvchilar
fetch('/api/leaderboard/top/10?sortBy=experience')
  .then(response => response.json())
  .then(data => {
    console.log('Top 10:', data.users);
  });

// Foydalanuvchi reytingi
fetch('/api/leaderboard/user/123/ranking')
  .then(response => response.json())
  .then(data => {
    console.log('User ranking:', data.ranking);
    console.log('User badge:', data.badge);
  });
```

### cURL Examples

```bash
# Leaderboard ro'yxati
curl "http://localhost:8080/api/leaderboard?page=0&size=10"

# Experience bo'yicha top 5
curl "http://localhost:8080/api/leaderboard/top/5?sortBy=experience"

# Username bo'yicha qidiruv
curl "http://localhost:8080/api/leaderboard?search=ali&page=0&size=10"

# Foydalanuvchi reytingi
curl "http://localhost:8080/api/leaderboard/user/123/ranking"
```

## Implementation Notes

1. **Public API**: Authentication talab qilinmaydi
2. **Pagination**: Sahifalash 0-dan boshlanadi
3. **Performance**: Database'da indexlar bo'lishi kerak
4. **Caching**: Redis bilan cache qilish tavsiya etiladi
5. **Real-time**: WebSocket orqali real-time yangilanishlar qo'shish mumkin

## Database Optimization

```sql
-- Performance uchun indexlar
CREATE INDEX idx_user_statistics_total_solved ON user_statistics(total_solved DESC);
CREATE INDEX idx_user_statistics_experience ON user_statistics(experience DESC);
CREATE INDEX idx_user_statistics_level ON user_statistics(level DESC, current_level_xp DESC);
CREATE INDEX idx_user_statistics_current_streak ON user_statistics(current_streak DESC);
CREATE INDEX idx_user_statistics_longest_streak ON user_statistics(longest_streak DESC);
CREATE INDEX idx_users_username ON users(username);
```