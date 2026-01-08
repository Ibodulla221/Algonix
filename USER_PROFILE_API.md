# User Profile API Documentation

Foydalanuvchi profili boshqaruvi uchun mukammal API'lar.

## Endpoints

### 1. GET /api/profile/me

Joriy foydalanuvchining profilini olish.

#### URL
```
GET /api/profile/me
```

#### Authentication
- **Required**: JWT token
- **Header**: `Authorization: Bearer <token>`

#### Response Format

```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "role": "USER",
  "firstName": "John",
  "lastName": "Doe",
  "fullName": "John Doe",
  "displayName": "John Doe",
  "bio": "Full-stack developer passionate about algorithms",
  "location": "Tashkent, Uzbekistan",
  "company": "Tech Solutions",
  "jobTitle": "Senior Developer",
  "website": "https://johndoe.dev",
  "githubUsername": "johndoe",
  "linkedinUrl": "https://linkedin.com/in/johndoe",
  "twitterUsername": "johndoe",
  "avatarUrl": "/api/files/avatars/johndoe_uuid.jpg",
  "isProfilePublic": true,
  "showEmail": false,
  "showLocation": true,
  "showCompany": true,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-07T15:30:00",
  "statistics": {
    "totalSolved": 85,
    "beginnerSolved": 20,
    "basicSolved": 25,
    "normalSolved": 25,
    "mediumSolved": 12,
    "hardSolved": 3,
    "acceptanceRate": 78.5,
    "ranking": 45,
    "reputation": 1250,
    "coins": 850,
    "experience": 2100,
    "level": 7,
    "currentLevelXp": 45,
    "currentStreak": 12,
    "longestStreak": 25,
    "weeklyStreak": 7,
    "monthlyStreak": 15,
    "lastLoginDate": "2024-01-07"
  }
}
```

### 2. GET /api/profile/{username}

Boshqa foydalanuvchi profilini ko'rish.

#### URL
```
GET /api/profile/{username}
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `username` | String | Yes | Foydalanuvchi nomi |

#### Authentication
- **Required**: No (Public endpoint, lekin private profil uchun auth kerak)

#### Response Format

```json
{
  "id": 2,
  "username": "alice",
  "firstName": "Alice",
  "lastName": "Smith",
  "fullName": "Alice Smith",
  "displayName": "Alice Smith",
  "bio": "Algorithm enthusiast and competitive programmer",
  "location": "Samarkand, Uzbekistan",
  "company": "StartupXYZ",
  "avatarUrl": "/api/files/avatars/alice_uuid.jpg",
  "createdAt": "2023-12-15T08:00:00",
  "statistics": {
    "totalSolved": 120,
    "acceptanceRate": 85.2,
    "level": 9,
    "currentStreak": 18
  }
}
```

**Note**: Private ma'lumotlar (email, settings) faqat o'z profilida ko'rinadi.

### 3. PUT /api/profile/me

Profil ma'lumotlarini yangilash.

#### URL
```
PUT /api/profile/me
```

#### Authentication
- **Required**: JWT token
- **Header**: `Authorization: Bearer <token>`

#### Request Body

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "newemail@example.com",
  "bio": "Updated bio description",
  "location": "Tashkent, Uzbekistan",
  "company": "New Company",
  "jobTitle": "Lead Developer",
  "website": "https://newwebsite.com",
  "githubUsername": "newgithub",
  "linkedinUrl": "https://linkedin.com/in/newprofile",
  "twitterUsername": "newtwitter",
  "isProfilePublic": true,
  "showEmail": false,
  "showLocation": true,
  "showCompany": true
}
```

#### Validation Rules

| Field | Max Length | Required | Notes |
|-------|------------|----------|-------|
| `firstName` | 50 | No | - |
| `lastName` | 50 | No | - |
| `email` | - | No | Must be valid email format |
| `bio` | 500 | No | - |
| `location` | 100 | No | - |
| `company` | 100 | No | - |
| `jobTitle` | 100 | No | - |
| `website` | 200 | No | - |
| `githubUsername` | 50 | No | - |
| `linkedinUrl` | 200 | No | - |
| `twitterUsername` | 50 | No | - |

#### Response Format

Yangilangan profil ma'lumotlari (GET /api/profile/me formatida).

### 4. POST /api/profile/me/change-password

Parolni o'zgartirish.

#### URL
```
POST /api/profile/me/change-password
```

#### Authentication
- **Required**: JWT token
- **Header**: `Authorization: Bearer <token>`

#### Request Body

```json
{
  "currentPassword": "oldpassword123",
  "newPassword": "newpassword123",
  "confirmPassword": "newpassword123"
}
```

#### Validation Rules

| Field | Min Length | Required | Notes |
|-------|------------|----------|-------|
| `currentPassword` | - | Yes | Must match current password |
| `newPassword` | 6 | Yes | - |
| `confirmPassword` | - | Yes | Must match newPassword |

#### Response Format

```json
{
  "message": "Password changed successfully"
}
```

### 5. POST /api/profile/me/avatar

Avatar rasmini yuklash.

#### URL
```
POST /api/profile/me/avatar
```

#### Authentication
- **Required**: JWT token
- **Header**: `Authorization: Bearer <token>`

#### Request Format

**Content-Type**: `multipart/form-data`

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `file` | File | Yes | Rasm fayli (JPG, PNG, GIF) |

#### File Requirements

- **Format**: JPG, PNG, GIF
- **Max Size**: 5MB (recommended)
- **Dimensions**: Any (recommended: 200x200px yoki yuqori)

#### Response Format

Yangilangan profil ma'lumotlari (avatarUrl bilan).

### 6. DELETE /api/profile/me/avatar

Avatar rasmini o'chirish.

#### URL
```
DELETE /api/profile/me/avatar
```

#### Authentication
- **Required**: JWT token
- **Header**: `Authorization: Bearer <token>`

#### Response Format

```json
{
  "message": "Avatar deleted successfully"
}
```

### 7. GET /api/files/avatars/{filename}

Avatar rasmini olish (public endpoint).

#### URL
```
GET /api/files/avatars/{filename}
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `filename` | String | Yes | Avatar fayl nomi |

#### Authentication
- **Required**: No (Public endpoint)

#### Response Format

Binary rasm fayli (JPG/PNG/GIF).

## Response Fields

### UserProfileResponse

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Foydalanuvchi ID'si |
| `username` | String | Foydalanuvchi nomi |
| `email` | String | Email (privacy settings'ga qarab) |
| `role` | String | Foydalanuvchi roli |
| `firstName` | String | Ism |
| `lastName` | String | Familiya |
| `fullName` | String | To'liq ism |
| `displayName` | String | Ko'rsatiladigan ism |
| `bio` | String | Qisqa tavsif |
| `location` | String | Joylashuv |
| `company` | String | Kompaniya |
| `jobTitle` | String | Lavozim |
| `website` | String | Shaxsiy website |
| `githubUsername` | String | GitHub username |
| `linkedinUrl` | String | LinkedIn profil |
| `twitterUsername` | String | Twitter username |
| `avatarUrl` | String | Avatar rasm URL'i |
| `isProfilePublic` | Boolean | Profil ochiq/yopiq (faqat o'z profili) |
| `showEmail` | Boolean | Email ko'rsatish (faqat o'z profili) |
| `showLocation` | Boolean | Joylashuvni ko'rsatish (faqat o'z profili) |
| `showCompany` | Boolean | Kompaniyani ko'rsatish (faqat o'z profili) |
| `createdAt` | DateTime | Ro'yxatdan o'tgan sana |
| `updatedAt` | DateTime | Oxirgi yangilanish |
| `statistics` | Object | Foydalanuvchi statistikasi |

### UserStatisticsDto

| Field | Type | Description |
|-------|------|-------------|
| `totalSolved` | Integer | Jami yechilgan masalalar |
| `beginnerSolved` | Integer | Beginner masalalar |
| `basicSolved` | Integer | Basic masalalar |
| `normalSolved` | Integer | Normal masalalar |
| `mediumSolved` | Integer | Medium masalalar |
| `hardSolved` | Integer | Hard masalalar |
| `acceptanceRate` | Double | Qabul qilish foizi |
| `ranking` | Integer | Global reyting |
| `reputation` | Integer | Obro' ballari |
| `coins` | Integer | Coin balansi |
| `experience` | Integer | Tajriba ballari |
| `level` | Integer | Foydalanuvchi darajasi |
| `currentLevelXp` | Integer | Joriy darajadagi XP |
| `currentStreak` | Integer | Joriy streak |
| `longestStreak` | Integer | Eng uzun streak |
| `weeklyStreak` | Integer | Haftalik streak |
| `monthlyStreak` | Integer | Oylik streak |
| `lastLoginDate` | Date | Oxirgi kirgan sana |

## Error Responses

### 401 Unauthorized
```json
{
  "error": "Authentication required"
}
```

### 404 Not Found
```json
{
  "error": "User not found"
}
```

### 403 Forbidden
```json
{
  "error": "Profile is private"
}
```

### 400 Bad Request
```json
{
  "error": "Current password is incorrect"
}
```

## Usage Examples

### Frontend Integration

```javascript
// Joriy foydalanuvchi profilini olish
fetch('/api/profile/me', {
  headers: {
    'Authorization': 'Bearer ' + token
  }
})
.then(response => response.json())
.then(profile => {
  console.log('User profile:', profile);
});

// Profil yangilash
fetch('/api/profile/me', {
  method: 'PUT',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + token
  },
  body: JSON.stringify({
    firstName: 'John',
    lastName: 'Doe',
    bio: 'Updated bio'
  })
})
.then(response => response.json())
.then(updatedProfile => {
  console.log('Updated profile:', updatedProfile);
});

// Avatar yuklash
const formData = new FormData();
formData.append('file', avatarFile);

fetch('/api/profile/me/avatar', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + token
  },
  body: formData
})
.then(response => response.json())
.then(updatedProfile => {
  console.log('Avatar uploaded:', updatedProfile.avatarUrl);
});
```

### cURL Examples

```bash
# Joriy profil
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8080/api/profile/me"

# Boshqa foydalanuvchi profili
curl "http://localhost:8080/api/profile/johndoe"

# Profil yangilash
curl -X PUT \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"firstName":"John","bio":"New bio"}' \
  "http://localhost:8080/api/profile/me"

# Avatar yuklash
curl -X POST \
  -H "Authorization: Bearer <token>" \
  -F "file=@avatar.jpg" \
  "http://localhost:8080/api/profile/me/avatar"

# Parol o'zgartirish
curl -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"currentPassword":"old","newPassword":"new","confirmPassword":"new"}' \
  "http://localhost:8080/api/profile/me/change-password"
```

## Implementation Notes

1. **File Upload**: Avatar fayllar `uploads/avatars/` papkasida saqlanadi
2. **Privacy**: Private profil faqat o'z egasi ko'ra oladi
3. **Validation**: Barcha input'lar validate qilinadi
4. **Security**: Parol o'zgartirish uchun joriy parol talab qilinadi
5. **File Management**: Eski avatar o'chiriladi yangi yuklanganda

## Database Schema Updates

```sql
-- UserEntity jadvaliga yangi ustunlar qo'shish
ALTER TABLE users ADD COLUMN first_name VARCHAR(50);
ALTER TABLE users ADD COLUMN last_name VARCHAR(50);
ALTER TABLE users ADD COLUMN bio TEXT;
ALTER TABLE users ADD COLUMN location VARCHAR(100);
ALTER TABLE users ADD COLUMN company VARCHAR(100);
ALTER TABLE users ADD COLUMN job_title VARCHAR(100);
ALTER TABLE users ADD COLUMN website VARCHAR(200);
ALTER TABLE users ADD COLUMN github_username VARCHAR(50);
ALTER TABLE users ADD COLUMN linkedin_url VARCHAR(200);
ALTER TABLE users ADD COLUMN twitter_username VARCHAR(50);
ALTER TABLE users ADD COLUMN avatar_url VARCHAR(500);
ALTER TABLE users ADD COLUMN avatar_file_name VARCHAR(255);
ALTER TABLE users ADD COLUMN is_profile_public BOOLEAN DEFAULT TRUE;
ALTER TABLE users ADD COLUMN show_email BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN show_location BOOLEAN DEFAULT TRUE;
ALTER TABLE users ADD COLUMN show_company BOOLEAN DEFAULT TRUE;
ALTER TABLE users ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE users ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
```