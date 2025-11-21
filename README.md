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
- Database username/password
- JWT secret (minimum 256-bit)
- Email credentials (Gmail App Password kerak)
- CORS allowed origins

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

## Muhim Eslatmalar

⚠️ **Production uchun:**
- `application.properties` faylini git'ga commit qilmang
- JWT secret'ni environment variable sifatida saqlang
- Database parollarini xavfsiz joyda saqlang
- CORS allowed origins'ni to'g'ri sozlang
- `spring.jpa.show-sql=false` qiling

## Muallif

Algonix Development Team
