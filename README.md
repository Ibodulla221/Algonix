# Algonix - Coding Platform

Algonix - bu dasturlash muammolarini yechish uchun platforma. Spring Boot va PostgreSQL asosida qurilgan.

## Texnologiyalar

- Java 17+
- Spring Boot 3.x
- Spring Security + JWT
- PostgreSQL
- Docker (kod bajarish uchun)
- Swagger/OpenAPI

## Sozlash

### 1. Ma'lumotlar bazasini yaratish

```sql
CREATE DATABASE algonix;
```

### 2. Application properties sozlash

`application.properties.example` faylini `application.properties` ga nusxalang va o'z ma'lumotlaringizni kiriting:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Kerakli sozlamalarni o'zgartiring:
- **Database**: `spring.datasource.password` - PostgreSQL paroli
- **JWT Secret**: `jwt.secret` - Minimum 256-bit base64 encoded secret
- **Email**: `spring.mail.username` va `spring.mail.password` (Gmail App Password)
- **CORS**: `cors.allowed-origins` - Frontend URL'lari
- **Frontend URL**: `app.frontend.url` - Password reset uchun

**JWT Secret yaratish:**
```bash
# Linux/Mac
openssl rand -base64 32

# Windows (PowerShell)
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

**Gmail App Password olish:**
1. Google Account Settings > Security
2. 2-Step Verification yoqing
3. App Passwords yarating (16 ta belgi)
4. Olingan parolni `spring.mail.password` ga kiriting

**Email validation:**
- Loyiha ishga tushganda email configuration avtomatik tekshiriladi
- Agar email yoki parol noto'g'ri bo'lsa, console'da ogohlantirish chiqadi
- `email.validation.enabled=false` qilib, validationni o'chirib qo'yish mumkin

### 3. Loyihani ishga tushirish

```bash
./mvnw spring-boot:run
```

yoki Windows uchun:

```bash
mvnw.cmd spring-boot:run
```

## API Endpoints

### Authentication

- `POST /api/auth/register` - Ro'yxatdan o'tish
- `POST /api/auth/login` - Tizimga kirish
- `POST /api/auth/refresh` - Token yangilash
- `POST /api/auth/forgot-password` - Parolni unutdim
- `POST /api/auth/reset-password` - Parolni tiklash

### Problems

- `GET /api/problems` - Barcha muammolarni ko'rish
- `POST /api/problems` - Yangi muammo qo'shish (USER, ADMIN)
- `PUT /api/problems/{id}` - Muammoni yangilash (ADMIN)
- `DELETE /api/problems/{id}` - Muammoni o'chirish (ADMIN)

### Submissions

- `POST /api/submissions` - Kod yuborish (USER, ADMIN)
- `GET /api/submissions` - O'z submissionlaringizni ko'rish

## Swagger UI

Loyiha ishga tushgandan keyin Swagger UI ga kirish:

```
http://localhost:8080/swagger-ui.html
```

## Xavfsizlik

- JWT tokenlar bilan autentifikatsiya
- BCrypt bilan parol shifrlash
- Role-based authorization (USER, ADMIN)
- CORS sozlamalari
- Token expiration handling

## Email Sozlash

Gmail uchun App Password olish:
1. Google Account Settings > Security
2. 2-Step Verification yoqing
3. App Passwords yarating
4. Olingan parolni `application.properties` ga kiriting

## Docker Sozlash

Kod bajarish uchun Docker kerak:

```bash
# Docker o'rnatilganligini tekshirish
docker --version

# Docker ishga tushirish
# Windows: Docker Desktop'ni ishga tushiring
# Linux: sudo systemctl start docker
```

## Muhim Eslatmalar

⚠️ **Production uchun:**
- `application.properties` faylini git'ga commit qilmang (`.gitignore`da bor)
- Environment variable'lardan foydalaning
- JWT secret'ni xavfsiz saqlang (minimum 256-bit)
- Database parollarini xavfsiz joyda saqlang
- CORS allowed origins'ni to'g'ri sozlang
- `spring.jpa.show-sql=false` qiling
- Docker xavfsizlik sozlamalarini tekshiring

⚠️ **Xavfsizlik:**
- Default admin user: `username: admin, password: admin123`
- Test user: `username: testuser, password: test123`
- **Production'da bu parollarni o'zgartiring!**

## Yangi Xususiyatlar

✅ **Kod bajarish tizimi:**
- Docker orqali xavfsiz kod bajarish
- 15+ dasturlash tilini qo'llab-quvvatlash
- Test case'larni avtomatik tekshirish
- Compile va runtime xatolarini aniqlash
- Timeout va memory limit

✅ **Validation:**
- Input ma'lumotlarni tekshirish
- Email format validatsiyasi
- Password strength tekshirish
- Username pattern validatsiyasi

✅ **Error Handling:**
- Global exception handler
- To'g'ri error response'lar
- Logging (SLF4J)
- User-friendly xato xabarlari

✅ **Security:**
- Environment variable'lar
- Sensitive ma'lumotlarni yashirish
- .gitignore sozlamalari

## Muallif

Algonix Development Team
