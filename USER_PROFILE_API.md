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

### 3. PATCH /api/profile/me

Profil ma'lumotlarini yangilash.

#### URL
```
PATCH /api/profile/me
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

### 8. GET /api/profile/me/difficulty-stats

Foydalanuvchi masala qiyinchilik darajalari statistikasi.

#### URL
```
GET /api/profile/me/difficulty-stats
```

#### Authentication
- **Required**: JWT token
- **Header**: `Authorization: Bearer <token>`

#### Response Format

```json
{
  "allProblems": 22,
  "allUserSolvedProblems": 1,
  "difficultyStats": [
    {
      "name": "BEGINNER",
      "total": 5,
      "solved": 1
    },
    {
      "name": "BASIC",
      "total": 5,
      "solved": 0
    },
    {
      "name": "NORMAL",
      "total": 5,
      "solved": 0
    },
    {
      "name": "MEDIUM",
      "total": 2,
      "solved": 0
    },
    {
      "name": "HARD",
      "total": 5,
      "solved": 0
    }
  ]
}
```

### 9. GET /api/profile/me/category-stats

Foydalanuvchi masala category'lari statistikasi.

#### URL
```
GET /api/profile/me/category-stats
```

#### Authentication
- **Required**: JWT token
- **Header**: `Authorization: Bearer <token>`

#### Response Format

```json
{
  "allProblems": 22,
  "allUserSolvedProblems": 1,
  "categoryStats": [
    {
      "name": "array",
      "total": 8,
      "solved": 1
    },
    {
      "name": "hash-table",
      "total": 5,
      "solved": 0
    },
    {
      "name": "string",
      "total": 6,
      "solved": 0
    },
    {
      "name": "math",
      "total": 3,
      "solved": 0
    }
  ]
}
```

### 11. GET /api/profile/me/daily-problem-stats

Foydalanuvchi kunlik masala yechish statistikasi.

#### URL
```
GET /api/profile/me/daily-problem-stats?year=2026&month=1
```

#### Authentication
- **Required**: JWT token
- **Header**: `Authorization: Bearer <token>`

#### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `year` | Integer | Yes | Yil (masalan: 2026) |
| `month` | Integer | Yes | Oy (1-12) |

#### Response Format

```json
{
  "month": 1,
  "year": 2026,
  "values": [0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
  "totalProblems": 1,
  "monthName": "January",
  "title": "Daily Problems Solved in January 2026",
  "labels": ["1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"]
}
```

### 12. GET /api/files/avatars/{filename}

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
  method: 'PATCH',
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

// Avatar yuklash - File input orqali
function uploadAvatar() {
  const fileInput = document.createElement('input');
  fileInput.type = 'file';
  fileInput.accept = 'image/*'; // Faqat rasm fayllar
  fileInput.multiple = false;
  
  fileInput.onchange = function(event) {
    const file = event.target.files[0];
    if (file) {
      // File size check (5MB limit)
      if (file.size > 5 * 1024 * 1024) {
        alert('File size should be less than 5MB');
        return;
      }
      
      // File type check
      if (!file.type.startsWith('image/')) {
        alert('Please select an image file');
        return;
      }
      
      const formData = new FormData();
      formData.append('file', file);
      
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
        // Update UI with new avatar
        document.getElementById('avatar-img').src = updatedProfile.avatarUrl;
      })
      .catch(error => {
        console.error('Avatar upload failed:', error);
        alert('Avatar upload failed');
      });
    }
  };
  
  fileInput.click(); // Open file dialog
}

// Avatar o'chirish
function deleteAvatar() {
  if (confirm('Are you sure you want to delete your avatar?')) {
    fetch('/api/profile/me/avatar', {
      method: 'DELETE',
      headers: {
        'Authorization': 'Bearer ' + token
      }
    })
    .then(response => response.json())
    .then(result => {
      console.log('Avatar deleted:', result.message);
      // Update UI - remove avatar
      document.getElementById('avatar-img').src = '/default-avatar.png';
    })
    .catch(error => {
      console.error('Avatar delete failed:', error);
    });
  }
}

// HTML example
/*
<div class="avatar-section">
  <img id="avatar-img" src="/default-avatar.png" alt="User Avatar" class="avatar-image">
  <button onclick="uploadAvatar()" class="btn btn-primary">Upload Avatar</button>
  <button onclick="deleteAvatar()" class="btn btn-danger">Delete Avatar</button>
</div>
*/
```

### React Example

```jsx
import React, { useState } from 'react';

function AvatarUpload({ token, currentAvatarUrl, onAvatarUpdate }) {
  const [uploading, setUploading] = useState(false);

  const handleFileSelect = (event) => {
    const file = event.target.files[0];
    if (file) {
      uploadAvatar(file);
    }
  };

  const uploadAvatar = async (file) => {
    // Validate file
    if (file.size > 5 * 1024 * 1024) {
      alert('File size should be less than 5MB');
      return;
    }

    if (!file.type.startsWith('image/')) {
      alert('Please select an image file');
      return;
    }

    setUploading(true);

    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await fetch('/api/profile/me/avatar', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      });

      if (response.ok) {
        const updatedProfile = await response.json();
        onAvatarUpdate(updatedProfile.avatarUrl);
        alert('Avatar uploaded successfully!');
      } else {
        throw new Error('Upload failed');
      }
    } catch (error) {
      console.error('Avatar upload error:', error);
      alert('Avatar upload failed');
    } finally {
      setUploading(false);
    }
  };

  const deleteAvatar = async () => {
    if (!window.confirm('Are you sure you want to delete your avatar?')) {
      return;
    }

    try {
      const response = await fetch('/api/profile/me/avatar', {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        onAvatarUpdate(null);
        alert('Avatar deleted successfully!');
      } else {
        throw new Error('Delete failed');
      }
    } catch (error) {
      console.error('Avatar delete error:', error);
      alert('Avatar delete failed');
    }
  };

  return (
    <div className="avatar-upload">
      <div className="avatar-preview">
        <img 
          src={currentAvatarUrl || '/default-avatar.png'} 
          alt="User Avatar" 
          className="avatar-image"
        />
      </div>
      
      <div className="avatar-actions">
        <input
          type="file"
          accept="image/*"
          onChange={handleFileSelect}
          style={{ display: 'none' }}
          id="avatar-input"
        />
        
        <label 
          htmlFor="avatar-input" 
          className="btn btn-primary"
          style={{ cursor: uploading ? 'not-allowed' : 'pointer' }}
        >
          {uploading ? 'Uploading...' : 'Upload Avatar'}
        </label>
        
        {currentAvatarUrl && (
          <button 
            onClick={deleteAvatar}
            className="btn btn-danger"
            disabled={uploading}
          >
            Delete Avatar
          </button>
        )}
      </div>
    </div>
  );
}

export default AvatarUpload;
```

### Angular Example

```typescript
// avatar-upload.component.ts
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-avatar-upload',
  template: `
    <div class="avatar-upload">
      <div class="avatar-preview">
        <img [src]="currentAvatarUrl || '/default-avatar.png'" 
             alt="User Avatar" 
             class="avatar-image">
      </div>
      
      <div class="avatar-actions">
        <input type="file" 
               accept="image/*" 
               (change)="onFileSelected($event)"
               #fileInput
               style="display: none">
        
        <button (click)="fileInput.click()" 
                [disabled]="uploading"
                class="btn btn-primary">
          {{ uploading ? 'Uploading...' : 'Upload Avatar' }}
        </button>
        
        <button *ngIf="currentAvatarUrl" 
                (click)="deleteAvatar()"
                [disabled]="uploading"
                class="btn btn-danger">
          Delete Avatar
        </button>
      </div>
    </div>
  `
})
export class AvatarUploadComponent {
  @Input() token: string = '';
  @Input() currentAvatarUrl: string | null = null;
  @Output() avatarUpdated = new EventEmitter<string | null>();
  
  uploading = false;

  constructor(private http: HttpClient) {}

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.uploadAvatar(file);
    }
  }

  uploadAvatar(file: File) {
    // Validate file
    if (file.size > 5 * 1024 * 1024) {
      alert('File size should be less than 5MB');
      return;
    }

    if (!file.type.startsWith('image/')) {
      alert('Please select an image file');
      return;
    }

    this.uploading = true;

    const formData = new FormData();
    formData.append('file', file);

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.token}`
    });

    this.http.post<any>('/api/profile/me/avatar', formData, { headers })
      .subscribe({
        next: (response) => {
          this.avatarUpdated.emit(response.avatarUrl);
          alert('Avatar uploaded successfully!');
          this.uploading = false;
        },
        error: (error) => {
          console.error('Avatar upload error:', error);
          alert('Avatar upload failed');
          this.uploading = false;
        }
      });
  }

  deleteAvatar() {
    if (!confirm('Are you sure you want to delete your avatar?')) {
      return;
    }

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.token}`
    });

    this.http.delete('/api/profile/me/avatar', { headers })
      .subscribe({
        next: () => {
          this.avatarUpdated.emit(null);
          alert('Avatar deleted successfully!');
        },
        error: (error) => {
          console.error('Avatar delete error:', error);
          alert('Avatar delete failed');
        }
      });
  }
}
```

### cURL Examples

```bash
# Joriy profil
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8080/api/profile/me"

# Boshqa foydalanuvchi profili
curl "http://localhost:8080/api/profile/johndoe"

# Profil yangilash
curl -X PATCH \
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