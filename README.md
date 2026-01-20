# Algonix - Coding Platform

Algonix - bu dasturlash muammolarini yechish uchun platforma. Spring Boot va PostgreSQL asosida qurilgan.

## Texnologiyalar

- Java 17+
- Spring Boot 3.x
- Spring Security + JWT
- PostgreSQL
- Multi-Language Native Execution (5 til: JavaScript, Java, Python, C++, PHP)
- Swagger/OpenAPI

## Tizim talablari

### Majburiy:
- **Java 17+** (JDK)
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Node.js** (JavaScript uchun)
- **Python 3.x** (Python uchun)

### Ixtiyoriy (qo'shimcha tillar uchun):
- **MinGW-w64** yoki **MSYS2** (C++ uchun)
- **PHP 8.x** (PHP uchun)

## O'rnatish va sozlash

### 1. Repository'ni clone qiling

```bash
git clone <repository-url>
cd algonix
```

### 2. Ma'lumotlar bazasini yaratish

```sql
CREATE DATABASE algonix;
CREATE USER algonix_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE algonix TO algonix_user;
```

### 3. Konfiguratsiya

`application.properties.example` faylini `application.properties` ga nusxalang:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

**Majburiy sozlamalar:**
```properties
# Database
spring.datasource.password=your_database_password

# JWT Secret (32 byte base64)
jwt.secret=your_jwt_secret_here
```

**Ixtiyoriy sozlamalar:**
```properties
# Email (password reset uchun)
spring.mail.username=your_email@gmail.com
spring.mail.password=your_gmail_app_password

# OAuth2 (social login uchun)
spring.security.oauth2.client.registration.github.client-id=your_github_client_id
spring.security.oauth2.client.registration.github.client-secret=your_github_secret

# CORS (frontend uchun)
cors.allowed-origins=http://localhost:4200,http://your-frontend-url
```

### 4. Loyihani ishga tushirish

```bash
# Dependencies o'rnatish
mvn clean install

# Loyihani ishga tushirish
mvn spring-boot:run
```

### 5. API'ni tekshirish

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/api/system/health

## Qo'llab-quvvatlanadigan dasturlash tillari

| Til | Status | Talab |
|-----|--------|-------|
| JavaScript | ✅ Ishlaydi | Node.js |
| Java | ✅ Ishlaydi | JDK 17+ |
| Python | ✅ Ishlaydi | Python 3.x |
| PHP | ✅ Ishlaydi | PHP 8.x |
| C++ | ⚠️ Compiler kerak | MinGW-w64/MSYS2 |

## Environment Variables

Xavfsizlik uchun environment variables ishlatishingiz mumkin:

```bash
# Database
export DATABASE_URL=jdbc:postgresql://localhost:5432/algonix
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=your_password

# JWT
export JWT_SECRET=your_jwt_secret

# Email
export EMAIL_USERNAME=your_email@gmail.com
export EMAIL_PASSWORD=your_app_password

# OAuth2
export GITHUB_CLIENT_ID=your_github_id
export GITHUB_CLIENT_SECRET=your_github_secret
```

## Foydali ma'lumotlar

### JWT Secret yaratish:
```bash
# Linux/Mac
openssl rand -base64 32

# Windows (PowerShell)
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

### Gmail App Password olish:
1. Google Account Settings > Security
2. 2-Step Verification yoqing
3. App Passwords yarating
4. Olingan parolni ishlatting

### Test user:
- **Username**: `testuser`
- **Password**: `test123`

## Muammolarni hal qilish

### Port band bo'lsa:
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <process_id> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Database connection xatosi:
1. PostgreSQL ishlab turganini tekshiring
2. Database va user yaratilganini tekshiring
3. Password to'g'riligini tekshiring

### Code execution ishlamasa:
1. Kerakli dasturlash tillari o'rnatilganini tekshiring
2. PATH environment variable'da bo'lishini tekshiring
3. System info API orqali tekshiring: `/api/system/stats`

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

## Kod Bajarish Tizimi

Algonix 18 ta dasturlash tilini qo'llab-quvvatlaydi:

**Qo'llab-quvvatlanadigan tillar:**
- JavaScript, Python, Java, C++, C, C#
- Go, Rust, PHP, Ruby, Swift, Kotlin
- Scala, Perl, R, Dart, TypeScript, Bash

**Xususiyatlar:**
- Native ProcessBuilder orqali xavfsiz bajarish
- Xavfli komandalarni aniqlash va bloklash
- Resurs monitoring (CPU, Memory)
- Vaqt cheklovi va chiqish hajmini nazorat qilish

## Muhim Eslatmalar

⚠️ **Production uchun:**
- `application.properties` faylini git'ga commit qilmang (`.gitignore`da bor)
- Environment variable'lardan foydalaning
- JWT secret'ni xavfsiz saqlang (minimum 256-bit)
- Database parollarini xavfsiz joyda saqlang
- CORS allowed origins'ni to'g'ri sozlang
- `spring.jpa.show-sql=false` qiling
- Kod bajarish xavfsizlik sozlamalarini tekshiring

⚠️ **Xavfsizlik:**
- Default admin user: `username: admin, password: admin123`
- Test user: `username: testuser, password: test123`
- **Production'da bu parollarni o'zgartiring!**

## Yangi Xususiyatlar

✅ **Kod bajarish tizimi:**
- Native ProcessBuilder orqali xavfsiz kod bajarish
- 18 ta dasturlash tilini qo'llab-quvvatlash
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
