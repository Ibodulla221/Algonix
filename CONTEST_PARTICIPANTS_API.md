# Contest Participants API

## Endpoint: GET /api/contests/{contestId}/participants

Contest'ga register bo'lgan barcha ishtirokchilar ro'yxatini olish uchun API.

### URL
```
GET /api/contests/{contestId}/participants
```

### Parameters
- `contestId` (path parameter) - Contest ID (Long)

### Response
```json
[
  {
    "id": 1,
    "userId": 123,
    "username": "john_doe",
    "firstName": "John",
    "lastName": "Doe",
    "avatarUrl": "https://example.com/avatar.jpg",
    "registeredAt": "2024-01-15T10:30:00",
    "score": 250,
    "rank": 5,
    "ratingChange": 25,
    "problemsSolved": 3,
    "totalPenalty": 1800,
    "totalRating": 1450
  },
  {
    "id": 2,
    "userId": 456,
    "username": "jane_smith",
    "firstName": "Jane",
    "lastName": "Smith",
    "avatarUrl": null,
    "registeredAt": "2024-01-15T11:15:00",
    "score": 180,
    "rank": 8,
    "ratingChange": 10,
    "problemsSolved": 2,
    "totalPenalty": 2400,
    "totalRating": 1320
  }
]
```

### Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Participant ID |
| `userId` | Long | User ID |
| `username` | String | Foydalanuvchi nomi |
| `firstName` | String | Ism |
| `lastName` | String | Familiya |
| `avatarUrl` | String | Avatar rasm URL'i (null bo'lishi mumkin) |
| `registeredAt` | DateTime | Ro'yxatdan o'tgan vaqt |
| `score` | Integer | Contest'dagi ball |
| `rank` | Integer | Contest'dagi o'rin |
| `ratingChange` | Integer | Contest'dan keyin rating o'zgarishi |
| `problemsSolved` | Integer | Yechilgan masalalar soni |
| `totalPenalty` | Long | Jami penalty vaqti (sekundlarda) |
| `totalRating` | Integer | Barcha contest'lardan jami rating |

### HTTP Status Codes
- `200 OK` - Muvaffaqiyatli
- `404 Not Found` - Contest topilmadi

### Example Usage

#### JavaScript (Fetch API)
```javascript
fetch('/api/contests/123/participants')
  .then(response => response.json())
  .then(participants => {
    console.log('Contest participants:', participants);
    participants.forEach(p => {
      console.log(`${p.username}: ${p.score} points, rank ${p.rank}`);
    });
  });
```

#### cURL
```bash
curl -X GET "http://localhost:8080/api/contests/123/participants" \
     -H "Accept: application/json"
```

### Notes
- Bu endpoint authentication talab qilmaydi (public)
- Ishtirokchilar ro'yxatdan o'tgan vaqt bo'yicha tartiblangan
- Agar contest topilmasa, 404 xatosi qaytariladi
- Bo'sh array qaytariladi agar hech kim register bo'lmagan bo'lsa

### Farqi boshqa endpoint'lardan
- `/api/contests/{contestId}/standings` - Contest standings (rank bo'yicha tartiblangan)
- `/api/contests/{contestId}/participants` - Barcha ishtirokchilar (register vaqti bo'yicha)

### Security
- Public endpoint (authentication kerak emas)
- Faqat public ma'lumotlar ko'rsatiladi
- Sensitive ma'lumotlar (email, password) ko'rsatilmaydi