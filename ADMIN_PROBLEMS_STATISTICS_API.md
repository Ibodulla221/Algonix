# Admin Statistics API Documentation

Admin panel uchun masalalar va foydalanuvchilar statistikasi API'lari.

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

### 2. GET /api/admin/problems/chart-data

Masalalar grafigi uchun formatlangan ma'lumotlarni olish.

#### URL
```
GET /api/admin/problems/chart-data
```

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `year` | Integer | No | Current year | Grafik uchun yil |

### 3. GET /api/admin/problems/daily-stats

**NEW**: Belgilangan oy uchun kunlik masalalar statistikasi.

#### URL
```
GET /api/admin/problems/daily-stats
```

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `year` | Integer | Yes | - | Yil (masalan: 2024) |
| `month` | Integer | Yes | - | Oy (1-12) |

#### Request Examples

```http
GET /api/admin/problems/daily-stats?year=2024&month=4
```

#### Response Format

```json
{
  "labels": ["1", "2", "3", "4", "5", ..., "30"],
  "values": [0, 1, 2, 5, 3, 1, 0, 2, 4, 1, ...],
  "year": 2024,
  "month": 4,
  "monthName": "April",
  "title": "Daily Problems Created in April 2024",
  "totalProblems": 45
}
```

### 4. GET /api/admin/users/registration-stats

**NEW**: Foydalanuvchilar ro'yxatdan o'tish statistikasi.

#### URL
```
GET /api/admin/users/registration-stats
```

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `year` | Integer | No | - | Belgilangan yil uchun statistika |
| `type` | String | No | "monthly" | Statistika turi: "monthly" yoki "yearly" |

#### Request Examples

##### Joriy yilning oylik statistikasi
```http
GET /api/admin/users/registration-stats?year=2024&type=monthly
```

##### Barcha yillarning statistikasi
```http
GET /api/admin/users/registration-stats?type=yearly
```

#### Response Format (Monthly)

```json
{
  "year": 2024,
  "monthlyStats": {
    "1": 15,
    "2": 23,
    "3": 18,
    "4": 31,
    "5": 27,
    "6": 19,
    "7": 22,
    "8": 16,
    "9": 14,
    "10": 8,
    "11": 5,
    "12": 3
  },
  "roleStats": {
    "USER": {
      "1": 14,
      "2": 22,
      "3": 17,
      "4": 29,
      "5": 25,
      "6": 18,
      "7": 21,
      "8": 15,
      "9": 13,
      "10": 7,
      "11": 4,
      "12": 2
    },
    "ADMIN": {
      "1": 1,
      "2": 1,
      "3": 1,
      "4": 2,
      "5": 2,
      "6": 1,
      "7": 1,
      "8": 1,
      "9": 1,
      "10": 1,
      "11": 1,
      "12": 1
    }
  },
  "totalForYear": 201,
  "availableYears": [2024, 2023, 2022]
}
```

#### Response Format (Yearly)

```json
{
  "yearlyStats": [
    [2024, 201],
    [2023, 156],
    [2022, 89]
  ],
  "availableYears": [2024, 2023, 2022]
}
```

### 5. GET /api/admin/users/registration-chart-data

**NEW**: Foydalanuvchilar ro'yxatdan o'tish grafigi uchun ma'lumotlar.

#### URL
```
GET /api/admin/users/registration-chart-data
```

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `year` | Integer | No | Current year | Grafik uchun yil |

#### Request Examples

```http
GET /api/admin/users/registration-chart-data?year=2024
```

#### Response Format

```json
{
  "year": 2026,
  "availableYears": [2026],
  "USER": [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  "monthlyStats": [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  "labels": ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
  "totalForYear": 1
}
```

## Response Fields

### Daily Problems Statistics Response

| Field | Type | Description |
|-------|------|-------------|
| `labels` | Array | Oy kunlari (1, 2, 3, ..., 30/31) |
| `values` | Array | Har kun uchun masalalar soni |
| `year` | Integer | Tanlangan yil |
| `month` | Integer | Tanlangan oy (1-12) |
| `monthName` | String | Oy nomi (inglizcha) |
| `title` | String | Grafik sarlavhasi |
| `totalProblems` | Long | Oy davomida jami masalalar |

### User Registration Statistics Response

| Field | Type | Description |
|-------|------|-------------|
| `year` | Integer | Tanlangan yil |
| `monthlyStats` | Object | Oylik ro'yxatdan o'tish soni (1-12 oy) |
| `roleStats` | Object | Role bo'yicha oylik statistika |
| `totalForYear` | Long | Yil davomida jami ro'yxatdan o'tganlar |
| `availableYears` | Array | Mavjud yillar ro'yxati |

### User Registration Chart Data Response

| Field | Type | Description |
|-------|------|-------------|
| `year` | Integer | Grafik yili |
| `availableYears` | Array | Mavjud yillar ro'yxati |
| `USER` | Array | USER role'dagi foydalanuvchilar (12 oy uchun) |
| `monthlyStats` | Array | USER'lar oylik ro'yxatdan o'tish (12 oy uchun) |
| `labels` | Array | Oy nomlari (Jan, Feb, ...) |
| `totalForYear` | Integer | Yil davomida jami USER'lar |

## Error Responses

### 400 Bad Request
```json
{
  "error": "Month must be between 1 and 12"
}
```

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

#### Daily Problems Chart
```javascript
// Kunlik masalalar grafigi
fetch('/api/admin/problems/daily-stats?year=2024&month=4', {
  headers: {
    'Authorization': 'Bearer ' + adminToken
  }
})
.then(response => response.json())
.then(data => {
  const ctx = document.getElementById('dailyProblemsChart').getContext('2d');
  new Chart(ctx, {
    type: 'line',
    data: {
      labels: data.labels,
      datasets: [{
        label: data.title,
        data: data.values,
        borderColor: 'rgba(75, 192, 192, 1)',
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        tension: 0.1
      }]
    },
    options: {
      responsive: true,
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            stepSize: 1
          }
        }
      }
    }
  });
});
```

#### User Registration Chart
```javascript
// Foydalanuvchilar ro'yxatdan o'tish grafigi (faqat USER'lar)
fetch('/api/admin/users/registration-chart-data?year=2024', {
  headers: {
    'Authorization': 'Bearer ' + adminToken
  }
})
.then(response => response.json())
.then(data => {
  const ctx = document.getElementById('userRegistrationChart').getContext('2d');
  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: data.labels,
      datasets: [
        {
          label: 'New Users',
          data: data.USER,
          backgroundColor: 'rgba(54, 162, 235, 0.6)',
          borderColor: 'rgba(54, 162, 235, 1)',
          borderWidth: 1
        }
      ]
    },
    options: {
      responsive: true,
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            stepSize: 1
          }
        }
      }
    }
  });
  
  console.log('Total users registered for year:', data.totalForYear);
  console.log('Available years:', data.availableYears);
});
```

### cURL Examples

```bash
# Kunlik masalalar statistikasi
curl -H "Authorization: Bearer <admin_token>" \
  "http://localhost:8080/api/admin/problems/daily-stats?year=2024&month=4"

# Foydalanuvchilar ro'yxatdan o'tish statistikasi
curl -H "Authorization: Bearer <admin_token>" \
  "http://localhost:8080/api/admin/users/registration-stats?year=2024"

# Foydalanuvchilar grafik ma'lumotlari
curl -H "Authorization: Bearer <admin_token>" \
  "http://localhost:8080/api/admin/users/registration-chart-data?year=2024"

# Yillik foydalanuvchilar statistikasi
curl -H "Authorization: Bearer <admin_token>" \
  "http://localhost:8080/api/admin/users/registration-stats?type=yearly"
```

## Implementation Notes

1. **Database Queries**: PostgreSQL'ning `EXTRACT` funksiyasi ishlatiladi
2. **Performance**: Indexlar `created_at` ustunida bo'lishi kerak
3. **Data Format**: Oylar 1-12 raqamlarda, kunlar 1-31 raqamlarda
4. **Null Handling**: `created_at` NULL bo'lgan yozuvlar hisobga olinmaydi
5. **Authorization**: Faqat ADMIN role'ga ega foydalanuvchilar kirishi mumkin
6. **Month Validation**: Oy parametri 1-12 oralig'ida bo'lishi kerak

## Database Schema Requirements

```sql
-- Users va Problems jadvallarida created_at ustuni bo'lishi kerak
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE problems ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Performance uchun indexlar
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);
CREATE INDEX IF NOT EXISTS idx_problems_created_at ON problems(created_at);
CREATE INDEX IF NOT EXISTS idx_users_created_year ON users(EXTRACT(YEAR FROM created_at));
CREATE INDEX IF NOT EXISTS idx_problems_created_year ON problems(EXTRACT(YEAR FROM created_at));
CREATE INDEX IF NOT EXISTS idx_users_created_month ON users(EXTRACT(YEAR FROM created_at), EXTRACT(MONTH FROM created_at));
CREATE INDEX IF NOT EXISTS idx_problems_created_month ON problems(EXTRACT(YEAR FROM created_at), EXTRACT(MONTH FROM created_at));
```

### 6. GET /api/admin/months-range

**NEW**: Oy oralig'ini MM.YYYY formatida olish.

#### URL
```
GET /api/admin/months-range
```

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `startDate` | String | Yes | - | Boshlang'ich sana (YYYY-MM yoki MM.YYYY) |
| `count` | Integer | No | 12 | Nechta oy qaytarish |

#### Request Examples

```http
GET /api/admin/months-range?startDate=2025-10&count=4
GET /api/admin/months-range?startDate=10.2025&count=6
GET /api/admin/months-range?startDate=12.2025
```

#### Response Format

```json
["10.2025", "11.2025", "12.2025", "01.2026"]
```

#### Input Formats

| Format | Example | Description |
|--------|---------|-------------|
| `YYYY-MM` | `2025-10` | Yil-oy format |
| `MM.YYYY` | `10.2025` | Oy.yil format |

#### Validation Rules

- Oy: 1-12 oralig'ida
- Yil: 1900-2100 oralig'ida
- Count: Musbat son
- Format: YYYY-MM yoki MM.YYYY

#### Error Responses

```json
// 400 Bad Request - noto'g'ri format yoki qiymat
```

### 1. Admin Dashboard
- Oylik va yillik statistikalarni ko'rsatish
- Trend tahlili va o'sish ko'rsatkichlari
- Platform faolligini monitoring qilish

### 2. Business Analytics
- Foydalanuvchilar o'sish dinamikasi
- Masalalar yaratilish tezligi
- Seasonal patterns aniqlash

### 3. Performance Monitoring
- Platform load'ini bashorat qilish
- Resource planning uchun ma'lumotlar
- Growth metrics tracking

### cURL Examples for Months Range

```bash
# Months Range API (YYYY-MM format)
curl -H "Authorization: Bearer <admin_token>" \
  "http://localhost:8080/api/admin/months-range?startDate=2025-10&count=4"

# Months Range API (MM.YYYY format)
curl -H "Authorization: Bearer <admin_token>" \
  "http://localhost:8080/api/admin/months-range?startDate=10.2025&count=6"

# Default count (12 months)
curl -H "Authorization: Bearer <admin_token>" \
  "http://localhost:8080/api/admin/months-range?startDate=2025-12"
```

## Use Cases

### 1. Admin Dashboard
- Oylik va yillik statistikalarni ko'rsatish
- Trend tahlili va o'sish ko'rsatkichlari
- Platform faolligini monitoring qilish

### 2. Business Analytics
- Foydalanuvchilar o'sish dinamikasi
- Masalalar yaratilish tezligi
- Seasonal patterns aniqlash

### 3. Performance Monitoring
- Platform load'ini bashorat qilish
- Resource planning uchun ma'lumotlar
- Growth metrics tracking

### 4. Months Range Utility
- Frontend dropdown'lar uchun oy ro'yxati
- Date picker'lar uchun ma'lumotlar
- Report generation uchun oy oralig'i


## Yangi Endpoint'lar

### GET /api/admin/problems/yearly-stats

Yillik masalalar yaratilish statistikasi (oylik breakdown).

#### URL
```
GET /api/admin/problems/yearly-stats?year=2026
```

#### Authentication
- **Required**: JWT token (ADMIN role)
- **Header**: `Authorization: Bearer <token>`

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `year` | Integer | No | Current year | Statistika yili |

#### Response Format

```json
{
  "year": 2026,
  "availableYears": [2026],
  "values": [22,0,0,0,0,0,0,0,0,0,0,0],
  "title": "Problems Created in 2026",
  "labels": ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"]
}
```

#### Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `year` | Integer | Statistika yili |
| `availableYears` | Array | Mavjud yillar ro'yxati |
| `values` | Array | Har oy uchun yaratilgan masalalar soni (12 ta element) |
| `title` | String | Grafik sarlavhasi |
| `labels` | Array | Oylar nomlari (qisqartirilgan) |

### GET /api/admin/problems/daily-stats

Belgilangan oy uchun kunlik masalalar yaratilish statistikasi.

#### URL
```
GET /api/admin/problems/daily-stats?year=2026&month=1
```

#### Authentication
- **Required**: JWT token (ADMIN role)
- **Header**: `Authorization: Bearer <token>`

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `year` | Integer | Yes | Statistika yili |
| `month` | Integer | Yes | Oy (1-12) |

#### Response Format

```json
{
  "month": 1,
  "year": 2026,
  "values": [0,0,0,0,0,0,0,0,0,0,0,0,0,22,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
  "totalProblems": 22,
  "monthName": "January",
  "title": "Daily Problems Created in January 2026",
  "labels": ["1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"]
}
```

#### Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `month` | Integer | Oy raqami (1-12) |
| `year` | Integer | Yil |
| `values` | Array | Har kun uchun yaratilgan masalalar soni |
| `totalProblems` | Integer | Oydagi jami masalalar |
| `monthName` | String | Oy nomi (to'liq) |
| `title` | String | Grafik sarlavhasi |
| `labels` | Array | Kunlar (1-31) |

## Frontend Integration Examples

### React Example - Yearly Chart

```jsx
import React, { useState, useEffect } from 'react';
import { Line } from 'react-chartjs-2';

function YearlyProblemsChart({ token }) {
  const [chartData, setChartData] = useState(null);
  const [year, setYear] = useState(new Date().getFullYear());

  useEffect(() => {
    fetchYearlyStats();
  }, [year]);

  const fetchYearlyStats = async () => {
    try {
      const response = await fetch(
        `/api/admin/problems/yearly-stats?year=${year}`,
        {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );
      const data = await response.json();
      
      setChartData({
        labels: data.labels,
        datasets: [{
          label: 'Problems Created',
          data: data.values,
          borderColor: 'rgb(75, 192, 192)',
          backgroundColor: 'rgba(75, 192, 192, 0.2)',
          tension: 0.1
        }]
      });
    } catch (error) {
      console.error('Error fetching yearly stats:', error);
    }
  };

  return (
    <div>
      <h3>Problems Created in {year}</h3>
      {chartData && <Line data={chartData} />}
    </div>
  );
}
```

### React Example - Daily Chart

```jsx
import React, { useState, useEffect } from 'react';
import { Bar } from 'react-chartjs-2';

function DailyProblemsChart({ token }) {
  const [chartData, setChartData] = useState(null);
  const [year, setYear] = useState(new Date().getFullYear());
  const [month, setMonth] = useState(new Date().getMonth() + 1);

  useEffect(() => {
    fetchDailyStats();
  }, [year, month]);

  const fetchDailyStats = async () => {
    try {
      const response = await fetch(
        `/api/admin/problems/daily-stats?year=${year}&month=${month}`,
        {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );
      const data = await response.json();
      
      setChartData({
        labels: data.labels,
        datasets: [{
          label: 'Problems Created',
          data: data.values,
          backgroundColor: 'rgba(54, 162, 235, 0.5)',
          borderColor: 'rgb(54, 162, 235)',
          borderWidth: 1
        }]
      });
    } catch (error) {
      console.error('Error fetching daily stats:', error);
    }
  };

  return (
    <div>
      <h3>{chartData?.title || 'Daily Problems'}</h3>
      <p>Total: {chartData?.totalProblems || 0} problems</p>
      {chartData && <Bar data={chartData} />}
    </div>
  );
}
```


## Yechilgan Masalalar Statistikasi

### GET /api/admin/submissions/yearly-solved-stats

Yillik yechilgan masalalar statistikasi (oylik breakdown).

#### URL
```
GET /api/admin/submissions/yearly-solved-stats?year=2026
```

#### Authentication
- **Required**: JWT token (ADMIN role)
- **Header**: `Authorization: Bearer <token>`

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `year` | Integer | No | Current year | Statistika yili |

#### Response Format

```json
{
  "year": 2026,
  "availableYears": [2026],
  "values": [5,8,12,15,10,7,9,11,14,16,13,10],
  "title": "Problems Solved in 2026",
  "labels": ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"]
}
```

#### Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `year` | Integer | Statistika yili |
| `availableYears` | Array | Mavjud yillar ro'yxati |
| `values` | Array | Har oy uchun yechilgan masalalar soni (12 ta element) |
| `title` | String | Grafik sarlavhasi |
| `labels` | Array | Oylar nomlari (qisqartirilgan) |

### GET /api/admin/submissions/daily-solved-stats

Belgilangan oy uchun kunlik yechilgan masalalar statistikasi.

#### URL
```
GET /api/admin/submissions/daily-solved-stats?year=2026&month=1
```

#### Authentication
- **Required**: JWT token (ADMIN role)
- **Header**: `Authorization: Bearer <token>`

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `year` | Integer | Yes | Statistika yili |
| `month` | Integer | Yes | Oy (1-12) |

#### Response Format

```json
{
  "month": 1,
  "year": 2026,
  "values": [0,1,2,1,3,2,1,0,2,3,4,2,1,3,2,1,0,1,2,3,1,2,1,0,1,2,3,1,0,1,2],
  "totalProblems": 45,
  "monthName": "January",
  "title": "Problems Solved in January 2026",
  "labels": ["1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"]
}
```

#### Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `month` | Integer | Oy raqami (1-12) |
| `year` | Integer | Yil |
| `values` | Array | Har kun uchun yechilgan masalalar soni |
| `totalProblems` | Integer | Oydagi jami yechilgan masalalar |
| `monthName` | String | Oy nomi (to'liq) |
| `title` | String | Grafik sarlavhasi |
| `labels` | Array | Kunlar (1-31) |

## Farqi

### Yaratilgan vs Yechilgan Masalalar

**Yaratilgan masalalar (Problems Created):**
- `/api/admin/problems/yearly-stats` - Admin tomonidan yaratilgan masalalar
- `/api/admin/problems/daily-stats` - Kunlik yaratilgan masalalar

**Yechilgan masalalar (Problems Solved):**
- `/api/admin/submissions/yearly-solved-stats` - Foydalanuvchilar tomonidan yechilgan masalalar
- `/api/admin/submissions/daily-solved-stats` - Kunlik yechilgan masalalar

## Frontend Integration Example

```jsx
import React, { useState, useEffect } from 'react';
import { Line } from 'react-chartjs-2';

function SolvedProblemsChart({ token }) {
  const [chartData, setChartData] = useState(null);
  const [year, setYear] = useState(new Date().getFullYear());

  useEffect(() => {
    fetchSolvedStats();
  }, [year]);

  const fetchSolvedStats = async () => {
    try {
      const response = await fetch(
        `/api/admin/submissions/yearly-solved-stats?year=${year}`,
        {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );
      const data = await response.json();
      
      setChartData({
        labels: data.labels,
        datasets: [{
          label: 'Problems Solved',
          data: data.values,
          borderColor: 'rgb(54, 162, 235)',
          backgroundColor: 'rgba(54, 162, 235, 0.2)',
          tension: 0.1
        }]
      });
    } catch (error) {
      console.error('Error fetching solved stats:', error);
    }
  };

  return (
    <div>
      <h3>Problems Solved in {year}</h3>
      {chartData && <Line data={chartData} />}
    </div>
  );
}
```