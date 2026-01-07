# Admin Problems Statistics API Documentation

Admin panel uchun masalalar yaratilish statistikasi API'lari.

## Endpoints

### 1. GET /api/admin/problems/statistics

Masalalar yaratilish statistikasini olish uchun.

#### URL
```
GET /api/admin/problems/statistics
```

#### Authentication
- **Required**: JWT token with ADMIN role
- **Header**: `Authorization: Bearer <admin_token>`

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `year` | Integer | No | - | Belgilangan yil uchun statistika |
| `type` | String | No | "monthly" | Statistika turi: "monthly" yoki "yearly" |

#### Request Examples

##### Joriy yilning oylik statistikasi
```http
GET /api/admin/problems/statistics?year=2024&type=monthly
```

##### Barcha yillarning statistikasi
```http
GET /api/admin/problems/statistics?type=yearly
```

##### Umumiy oylik statistika
```http
GET /api/admin/problems/statistics
```

#### Response Format (Monthly)

```json
{
  "year": 2024,
  "monthlyStats": {
    "1": 2,
    "2": 3,
    "3": 4,
    "4": 10,
    "5": 4,
    "6": 3,
    "7": 3,
    "8": 2,
    "9": 1,
    "10": 0,
    "11": 0,
    "12": 0
  },
  "difficultyStats": {
    "BEGINNER": {
      "1": 1,
      "2": 2,
      "3": 1,
      "4": 3,
      "5": 2,
      "6": 1,
      "7": 1,
      "8": 1,
      "9": 0,
      "10": 0,
      "11": 0,
      "12": 0
    },
    "EASY": {
      "1": 1,
      "2": 1,
      "3": 2,
      "4": 4,
      "5": 1,
      "6": 1,
      "7": 1,
      "8": 1,
      "9": 1,
      "10": 0,
      "11": 0,
      "12": 0
    },
    "MEDIUM": {
      "1": 0,
      "2": 0,
      "3": 1,
      "4": 2,
      "5": 1,
      "6": 1,
      "7": 1,
      "8": 0,
      "9": 0,
      "10": 0,
      "11": 0,
      "12": 0
    },
    "HARD": {
      "1": 0,
      "2": 0,
      "3": 0,
      "4": 1,
      "5": 0,
      "6": 0,
      "7": 0,
      "8": 0,
      "9": 0,
      "10": 0,
      "11": 0,
      "12": 0
    }
  },
  "totalForYear": 32,
  "availableYears": [2024, 2023, 2022]
}
```

#### Response Format (Yearly)

```json
{
  "yearlyStats": [
    [2024, 32],
    [2023, 45],
    [2022, 28]
  ],
  "availableYears": [2024, 2023, 2022]
}
```

### 2. GET /api/admin/problems/chart-data

Grafik uchun formatlangan ma'lumotlarni olish.

#### URL
```
GET /api/admin/problems/chart-data
```

#### Authentication
- **Required**: JWT token with ADMIN role
- **Header**: `Authorization: Bearer <admin_token>`

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `year` | Integer | No | Current year | Grafik uchun yil |

#### Request Examples

```http
GET /api/admin/problems/chart-data?year=2024
```

#### Response Format

```json
{
  "labels": ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
  "values": [2.3, 3.1, 4.0, 10.1, 4.0, 3.6, 3.2, 2.3, 1.4, 0.8, 0.5, 0.2],
  "year": 2024,
  "title": "Problems Created in 2024",
  "availableYears": [2024, 2023, 2022]
}
```

## Response Fields

### Monthly Statistics Response

| Field | Type | Description |
|-------|------|-------------|
| `year` | Integer | Tanlangan yil |
| `monthlyStats` | Object | Oylik masalalar soni (1-12 oy) |
| `difficultyStats` | Object | Qiyinlik darajasi bo'yicha oylik statistika |
| `totalForYear` | Long | Yil davomida yaratilgan jami masalalar |
| `availableYears` | Array | Mavjud yillar ro'yxati |

### Yearly Statistics Response

| Field | Type | Description |
|-------|------|-------------|
| `yearlyStats` | Array | Yillik statistika [yil, soni] formatida |
| `availableYears` | Array | Mavjud yillar ro'yxati |

### Chart Data Response

| Field | Type | Description |
|-------|------|-------------|
| `labels` | Array | Oy nomlari (Jan, Feb, ...) |
| `values` | Array | Har oy uchun masalalar soni |
| `year` | Integer | Grafik yili |
| `title` | String | Grafik sarlavhasi |
| `availableYears` | Array | Mavjud yillar ro'yxati |

## Error Responses

### 401 Unauthorized
```json
{
  "error": "Authentication required"
}
```

### 403 Forbidden
```json
{
  "error": "Admin access required"
}
```

## Usage Examples

### Frontend Integration (Chart.js)

```javascript
// Grafik ma'lumotlarini olish
fetch('/api/admin/problems/chart-data?year=2024', {
  headers: {
    'Authorization': 'Bearer ' + adminToken
  }
})
.then(response => response.json())
.then(data => {
  // Chart.js bilan grafik yaratish
  const ctx = document.getElementById('problemsChart').getContext('2d');
  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: data.labels,
      datasets: [{
        label: data.title,
        data: data.values,
        backgroundColor: 'rgba(54, 162, 235, 0.6)',
        borderColor: 'rgba(54, 162, 235, 1)',
        borderWidth: 1
      }]
    },
    options: {
      responsive: true,
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  });
});
```

### cURL Examples

```bash
# Joriy yil statistikasi
curl -H "Authorization: Bearer <admin_token>" \
  "http://localhost:8080/api/admin/problems/chart-data"

# Belgilangan yil statistikasi
curl -H "Authorization: Bearer <admin_token>" \
  "http://localhost:8080/api/admin/problems/chart-data?year=2023"

# Batafsil oylik statistika
curl -H "Authorization: Bearer <admin_token>" \
  "http://localhost:8080/api/admin/problems/statistics?year=2024&type=monthly"

# Yillik statistika
curl -H "Authorization: Bearer <admin_token>" \
  "http://localhost:8080/api/admin/problems/statistics?type=yearly"
```

## Implementation Notes

1. **Database Queries**: PostgreSQL'ning `EXTRACT` funksiyasi ishlatiladi
2. **Performance**: Indexlar `created_at` ustunida bo'lishi kerak
3. **Data Format**: Oylar 1-12 raqamlarda, yillar to'liq formatda
4. **Null Handling**: `created_at` NULL bo'lgan masalalar hisobga olinmaydi
5. **Authorization**: Faqat ADMIN role'ga ega foydalanuvchilar kirishi mumkin

## Database Schema Requirements

```sql
-- Problems jadvalida created_at ustuni bo'lishi kerak
ALTER TABLE problems ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Performance uchun index
CREATE INDEX IF NOT EXISTS idx_problems_created_at ON problems(created_at);
CREATE INDEX IF NOT EXISTS idx_problems_created_year ON problems(EXTRACT(YEAR FROM created_at));
```