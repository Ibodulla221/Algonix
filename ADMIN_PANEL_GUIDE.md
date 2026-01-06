# 🔧 Algonix Admin Panel Guide

## Umumiy ma'lumot

Admin Panel - bu Algonix platformasini to'liq boshqarish uchun mo'ljallangan maxsus interface. Faqat ADMIN role-ga ega foydalanuvchilar kirishi mumkin.

## 🎯 Admin Panel Funksiyalari

### 📊 **Dashboard**
- Platform umumiy statistikalari
- Foydalanuvchilar soni (jami, faol, adminlar)
- Masalalar statistikasi (difficulty bo'yicha)
- Submission statistikalari
- Bugungi faollik

### 👥 **User Management**
- Barcha foydalanuvchilarni ko'rish
- Foydalanuvchi ma'lumotlarini tahrirlash
- Admin huquqlari berish
- Foydalanuvchilarni block qilish
- User statistikalarini ko'rish

### 📝 **Problem Management**
- Yangi masalalar yaratish
- Mavjud masalalarni tahrirlash
- Masalalarni o'chirish
- Problem statistikalarini ko'rish
- Publish/Unpublish qilish

### 📨 **Message System**
- Barcha foydalanuvchilarga xabar yuborish
- System announcement yaratish
- Notification management

### 📈 **Analytics & Reports**
- Kunlik submission statistikalari
- Difficulty bo'yicha masala taqsimoti
- Top foydalanuvchilar
- Platform growth metrics

## 🔐 **Admin Panel API Endpoints**

### **Dashboard**
```http
GET /api/admin/dashboard
```
**Response:**
```json
{
  "users": {
    "total": 150,
    "active": 145,
    "admins": 5
  },
  "problems": {
    "total": 50,
    "beginner": 15,
    "basic": 12,
    "normal": 10,
    "medium": 8,
    "hard": 5
  },
  "submissions": {
    "total": 2500,
    "accepted": 1200,
    "pending": 50,
    "today": 85,
    "acceptanceRate": 48.0
  }
}
```

### **User Management**
```http
# Barcha foydalanuvchilar
GET /api/admin/users?page=0&size=20&sortBy=id&sortDir=desc

# Foydalanuvchini admin qilish
PUT /api/admin/users/{userId}/make-admin

# Foydalanuvchini block qilish
PUT /api/admin/users/{userId}/block
```

### **Problem Management**
```http
# Barcha masalalar
GET /api/admin/problems?page=0&size=20&difficulty=MEDIUM

# Yangi masala yaratish
POST /api/admin/problems

# Masalani yangilash
PUT /api/admin/problems/{problemId}

# Masalani o'chirish
DELETE /api/admin/problems/{problemId}

# Masala statistikalari
GET /api/admin/problems/{problemId}/stats
```

### **Messaging**
```http
# Barcha foydalanuvchilarga xabar yuborish
POST /api/admin/broadcast-message
{
  "title": "Platform yangilanishi",
  "content": "Yangi funksiyalar qo'shildi!"
}
```

### **Analytics**
```http
GET /api/admin/analytics
```

## 🛡️ **Security & Permissions**

### **Role-based Access Control**
```java
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    // Faqat ADMIN role-ga ega foydalanuvchilar kirishi mumkin
}
```

### **Admin User yaratish**
1. Oddiy user yaratish
2. Database-da role-ni ADMIN ga o'zgartirish
3. Yoki API orqali admin qilish

```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
```

## 🎨 **Frontend Integration**

### **Admin Dashboard Component**
```typescript
interface AdminDashboard {
  users: {
    total: number;
    active: number;
    admins: number;
  };
  problems: {
    total: number;
    beginner: number;
    basic: number;
    normal: number;
    medium: number;
    hard: number;
  };
  submissions: {
    total: number;
    accepted: number;
    pending: number;
    today: number;
    acceptanceRate: number;
  };
}
```

### **User Management Table**
```typescript
interface AdminUser {
  id: number;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN';
  totalSolved: number;
  coins: number;
  level: number;
  currentStreak: number;
  lastLoginDate: string;
}
```

### **Problem Management**
```typescript
interface AdminProblem {
  id: number;
  title: string;
  difficulty: string;
  totalSubmissions: number;
  totalAccepted: number;
  acceptanceRate: number;
  likes: number;
  dislikes: number;
  createdAt: string;
}
```

## 📱 **Admin Panel Pages**

### **1. Dashboard Page**
- Overview cards (users, problems, submissions)
- Recent activity feed
- Quick stats charts
- System health indicators

### **2. Users Page**
- User list with pagination
- Search and filter options
- User actions (make admin, block, view profile)
- User statistics

### **3. Problems Page**
- Problem list with filters
- Create new problem button
- Edit/Delete actions
- Problem statistics

### **4. Analytics Page**
- Charts va graphs
- Export functionality
- Date range filters
- Performance metrics

### **5. Messages Page**
- Broadcast message form
- Message history
- Template management

## 🔧 **Setup Instructions**

### **1. Admin User yaratish**
```bash
# 1. Oddiy user yaratish
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","email":"admin@algonix.com","password":"admin123"}'

# 2. Database-da admin qilish
psql -d algonix -c "UPDATE users SET role = 'ADMIN' WHERE username = 'admin';"
```

### **2. Admin Panel Test qilish**
```bash
# 1. Admin login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. Dashboard olish
curl -X GET http://localhost:8080/api/admin/dashboard \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 🚀 **Advanced Features**

### **1. Bulk Operations**
- Ko'p foydalanuvchilarni bir vaqtda boshqarish
- Bulk message sending
- Mass problem import/export

### **2. Audit Logs**
- Admin actions logging
- User activity tracking
- System changes history

### **3. Advanced Analytics**
- User engagement metrics
- Problem difficulty analysis
- Performance trends

### **4. Content Moderation**
- User-generated content review
- Spam detection
- Inappropriate content filtering

## 📊 **Monitoring & Alerts**

### **System Health**
- Server performance
- Database connections
- API response times
- Error rates

### **Business Metrics**
- Daily active users
- Problem solving rates
- User retention
- Platform growth

## 🎯 **Best Practices**

### **Security**
- Regular admin password updates
- Two-factor authentication
- IP whitelisting
- Session management

### **Performance**
- Pagination for large datasets
- Caching for frequently accessed data
- Optimized database queries
- Background job processing

### **User Experience**
- Intuitive navigation
- Responsive design
- Fast loading times
- Clear error messages

## 🔮 **Future Enhancements**

1. **Real-time Dashboard** - WebSocket bilan live updates
2. **Advanced Reporting** - PDF/Excel export
3. **A/B Testing** - Feature flag management
4. **Multi-language Support** - Admin panel localization
5. **Mobile Admin App** - React Native yoki Flutter

## 📝 **Xulosa**

Admin Panel Algonix platformasini professional darajada boshqarish imkonini beradi. Barcha kerakli funksiyalar mavjud va kengaytirish uchun tayyor.

**Asosiy afzalliklar:**
✅ To'liq user management  
✅ Problem CRUD operations  
✅ Real-time analytics  
✅ Message broadcasting  
✅ Security va permissions  
✅ Scalable architecture  

Admin panel tayyor va production-da ishlatish mumkin!