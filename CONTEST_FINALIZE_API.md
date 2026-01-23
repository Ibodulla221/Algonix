# Contest Finalize API

## Endpoint: POST /api/contests/{contestId}/finalize-with-results

Contest yakunida barcha ishtirokchilarning natijalarini ball bo'yicha tartiblash va batafsil natijalarni olish.

### URL
```
POST /api/contests/{contestId}/finalize-with-results
```

### Authentication
- **Required**: Admin role
- **Header**: `Authorization: Bearer <jwt_token>`

### Parameters
- `contestId` (path parameter) - Contest ID (Long)

### Response
```json
{
  "contestId": 123,
  "contestTitle": "Weekly Contest #45",
  "finalizedAt": "2024-01-15T18:00:00",
  "totalParticipants": 150,
  "results": [
    {
      "rank": 1,
      "userId": 456,
      "username": "coder_pro",
      "firstName": "John",
      "lastName": "Doe",
      "avatarUrl": "https://example.com/avatar.jpg",
      "totalScore": 300,
      "problemsSolved": 3,
      "totalPenalty": 1200,
      "ratingChange": 75,
      "newTotalRating": 1875,
      "problemResults": [
        {
          "problemSymbol": "A",
          "problemTitle": "Two Sum",
          "points": 100,
          "solved": true,
          "attempts": 1,
          "timeTaken": 300,
          "penalty": 0
        },
        {
          "problemSymbol": "B",
          "problemTitle": "Binary Search",
          "points": 100,
          "solved": true,
          "attempts": 2,
          "timeTaken": 600,
          "penalty": 300
        },
        {
          "problemSymbol": "C",
          "problemTitle": "Dynamic Programming",
          "points": 100,
          "solved": true,
          "attempts": 1,
          "timeTaken": 900,
          "penalty": 0
        }
      ]
    },
    {
      "rank": 2,
      "userId": 789,
      "username": "algorithm_master",
      "firstName": "Jane",
      "lastName": "Smith",
      "avatarUrl": null,
      "totalScore": 250,
      "problemsSolved": 2,
      "totalPenalty": 1800,
      "ratingChange": 50,
      "newTotalRating": 1650,
      "problemResults": [
        {
          "problemSymbol": "A",
          "problemTitle": "Two Sum",
          "points": 100,
          "solved": true,
          "attempts": 1,
          "timeTaken": 450,
          "penalty": 0
        },
        {
          "problemSymbol": "B",
          "problemTitle": "Binary Search",
          "points": 100,
          "solved": true,
          "attempts": 3,
          "timeTaken": 750,
          "penalty": 600
        },
        {
          "problemSymbol": "C",
          "problemTitle": "Dynamic Programming",
          "points": 100,
          "solved": false,
          "attempts": 5,
          "timeTaken": null,
          "penalty": 0
        }
      ]
    }
  ]
}
```

### Response Fields

#### Main Response
| Field | Type | Description |
|-------|------|-------------|
| `contestId` | Long | Contest ID |
| `contestTitle` | String | Contest nomi |
| `finalizedAt` | DateTime | Yakunlangan vaqt |
| `totalParticipants` | Integer | Jami ishtirokchilar soni |
| `results` | Array | Ishtirokchilar natijalari (rank bo'yicha tartiblangan) |

#### Participant Result
| Field | Type | Description |
|-------|------|-------------|
| `rank` | Integer | O'rin (1-chi, 2-chi, ...) |
| `userId` | Long | Foydalanuvchi ID |
| `username` | String | Foydalanuvchi nomi |
| `firstName` | String | Ism |
| `lastName` | String | Familiya |
| `avatarUrl` | String | Avatar URL (null bo'lishi mumkin) |
| `totalScore` | Integer | Jami ball |
| `problemsSolved` | Integer | Yechilgan masalalar soni |
| `totalPenalty` | Long | Jami penalty vaqti (sekundlarda) |
| `ratingChange` | Integer | Rating o'zgarishi |
| `newTotalRating` | Integer | Yangi jami rating |
| `problemResults` | Array | Har bir masala bo'yicha natija |

#### Problem Result
| Field | Type | Description |
|-------|------|-------------|
| `problemSymbol` | String | Masala belgisi (A, B, C, ...) |
| `problemTitle` | String | Masala nomi |
| `points` | Integer | Masala uchun maksimal ball |
| `solved` | Boolean | Yechilganmi |
| `attempts` | Integer | Urinishlar soni |
| `timeTaken` | Long | Yechish vaqti (sekundlarda, null agar yechilmagan) |
| `penalty` | Integer | Noto'g'ri urinishlar uchun penalty (sekundlarda) |

### Ranking Algorithm

1. **Primary**: Jami ball (yuqori yaxshi)
2. **Secondary**: Jami penalty vaqti (kam yaxshi)
3. **Tertiary**: Yechilgan masalalar soni (ko'p yaxshi)

### Rating Calculation

Rating o'zgarishi quyidagi formula bo'yicha hisoblanadi:

- **Top 10%**: +50 + (score/2)
- **Top 25%**: +30 + (score/3)  
- **Top 50%**: +10 + (score/5)
- **Top 75%**: 0
- **Bottom 25%**: -10

### Penalty System

- Har bir noto'g'ri urinish uchun **5 daqiqa (300 sekund)** penalty
- Penalty faqat masala yechilgan bo'lsa qo'shiladi
- Yechilmagan masalalar uchun penalty yo'q

### HTTP Status Codes

- `200 OK` - Muvaffaqiyatli yakunlandi
- `400 Bad Request` - Contest hali tugamagan
- `401 Unauthorized` - Authentication kerak
- `403 Forbidden` - Admin huquqi kerak
- `404 Not Found` - Contest topilmadi

### Example Usage

#### JavaScript (Fetch API)
```javascript
fetch('/api/contests/123/finalize-with-results', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(results => {
  console.log('Contest finalized:', results);
  results.results.forEach((participant, index) => {
    console.log(`${participant.rank}. ${participant.username}: ${participant.totalScore} points`);
  });
});
```

#### cURL
```bash
curl -X POST "http://localhost:8080/api/contests/123/finalize-with-results" \
     -H "Authorization: Bearer <jwt_token>" \
     -H "Content-Type: application/json"
```

### Notes

- Contest faqat tugagandan keyin yakunlanishi mumkin
- Yakunlash jarayonida barcha ishtirokchilarning natijalari qayta hisoblanadi
- O'rinlar avtomatik ravishda ball va penalty bo'yicha belgilanadi
- Contest-only masalalar yakunlashdan keyin public bo'ladi
- Rating o'zgarishlari darhol qo'llaniladi

### Security

- Faqat admin foydalanuvchilar contest'ni yakunlay oladi
- JWT token orqali authentication
- Contest ID validation
- Input sanitization