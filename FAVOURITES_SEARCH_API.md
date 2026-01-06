# Favourites Search API Documentation

## Endpoint: GET /api/problems/favourites

Bu endpoint foydalanuvchining sevimli masalalarini olish va qidirish uchun ishlatiladi.

### URL
```
GET /api/problems/favourites
```

### Authentication
- **Required**: JWT token
- **Header**: `Authorization: Bearer <token>`

### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `page` | Integer | No | 0 | Sahifa raqami (0-dan boshlanadi) |
| `size` | Integer | No | 20 | Sahifadagi elementlar soni |
| `search` | String | No | - | Masala nomida qidiruv |
| `difficulty` | Enum | No | - | Qiyinlik darajasi: `BEGINNER`, `BASIC`, `NORMAL`, `MEDIUM`, `HARD` |
| `categories` | List<String> | No | - | Kategoriyalar ro'yxati |

### Request Examples

#### 1. Barcha sevimli masalalarni olish
```http
GET /api/problems/favourites?page=0&size=10
```

#### 2. Nom bo'yicha qidiruv
```http
GET /api/problems/favourites?search=two sum&page=0&size=10
```

#### 3. Qiyinlik darajasi bo'yicha filtrlash
```http
GET /api/problems/favourites?difficulty=MEDIUM&page=0&size=10
```

#### 4. Kategoriya bo'yicha filtrlash
```http
GET /api/problems/favourites?categories=array,hash-table&page=0&size=10
```

#### 5. Kombinatsiyalangan qidiruv
```http
GET /api/problems/favourites?search=sum&difficulty=EASY&categories=array&page=0&size=10
```

### Response Format

```json
{
  "problems": [
    {
      "sequenceNumber": 1,
      "id": 1,
      "slug": "two-sum",
      "title": "Two Sum",
      "difficulty": "EASY",
      "acceptanceRate": 45.2,
      "isPremium": false,
      "status": "solved",
      "isFavourite": true,
      "frequency": 85.5,
      "categories": ["array", "hash-table"],
      "timeLimitMs": 2000,
      "memoryLimitMb": 512
    }
  ],
  "total": 15,
  "page": 0,
  "pageSize": 10
}
```

### Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `problems` | Array | Sevimli masalalar ro'yxati |
| `total` | Long | Jami sevimli masalalar soni |
| `page` | Integer | Joriy sahifa raqami |
| `pageSize` | Integer | Sahifadagi elementlar soni |

#### Problem Object Fields

| Field | Type | Description |
|-------|------|-------------|
| `sequenceNumber` | Integer | Masalaning tartib raqami |
| `id` | Long | Masalaning ID'si |
| `slug` | String | Masalaning slug'i |
| `title` | String | Masalaning nomi |
| `difficulty` | String | Qiyinlik darajasi |
| `acceptanceRate` | Double | Qabul qilish foizi |
| `isPremium` | Boolean | Premium masala ekanligini ko'rsatadi |
| `status` | String | Foydalanuvchi holati: "solved", "attempted", "todo" |
| `isFavourite` | Boolean | Sevimli ekanligini ko'rsatadi (har doim true) |
| `frequency` | Double | Masalaning chastotasi |
| `categories` | Array | Kategoriyalar ro'yxati |
| `timeLimitMs` | Integer | Vaqt chegarasi (millisekundlarda) |
| `memoryLimitMb` | Integer | Xotira chegarasi (MB da) |

### Error Responses

#### 401 Unauthorized
```json
{
  "error": "Authentication required"
}
```

#### 404 Not Found
```json
{
  "error": "User not found"
}
```

### Usage Notes

1. **Pagination**: Sahifalash 0-dan boshlanadi
2. **Search**: Qidiruv case-insensitive va partial match qiladi
3. **Categories**: Bir nechta kategoriya vergul bilan ajratiladi
4. **Filtering**: Barcha parametrlar ixtiyoriy va kombinatsiya qilinishi mumkin
5. **Performance**: Database-level pagination va filtering ishlatiladi

### Implementation Details

- **Repository**: `FavouriteRepository` da yangi query metodlari qo'shildi
- **Service**: `ProblemService.searchFavouriteProblems()` metodi yaratildi
- **Controller**: `ProblemController.getFavouriteProblems()` metodi yangilandi
- **Backward Compatibility**: Eski API format'i saqlanib qoldi

### Test Examples

#### cURL Examples

```bash
# Barcha sevimlilar
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8080/api/problems/favourites"

# Qidiruv
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8080/api/problems/favourites?search=array"

# Filtrlash
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8080/api/problems/favourites?difficulty=MEDIUM&categories=array,string"
```