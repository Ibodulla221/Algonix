# Algonix - Boshqa kompyuterda o'rnatish

Bu qo'llanma Algonix loyihasini boshqa kompyuterda o'rnatish uchun.

## 1. Tizim talablari

### Majburiy:
- **Java 17+** (JDK)
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Node.js 16+** (JavaScript uchun)
- **Python 3.8+** (Python uchun)

### Ixtiyoriy:
- **PHP 8.x** (PHP uchun)
- **MinGW-w64** yoki **MSYS2** (C++ uchun)

## 2. O'rnatish bosqichlari

### 2.1. Repository clone qiling
```bash
git clone <your-repository-url>
cd algonix
```

### 2.2. PostgreSQL o'rnating va sozlang

#### Windows:
1. [PostgreSQL](https://www.postgresql.org/download/windows/) yuklab oling
2. O'rnating va parol o'rnating
3. pgAdmin orqali database yarating:

```sql
CREATE DATABASE algonix;
```

#### Linux (Ubuntu/Debian):
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo -u postgres psql
CREATE DATABASE algonix;
CREATE USER algonix_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE algonix TO algonix_user;
\q
```

### 2.3. Java va Maven o'rnating

#### Windows:
1. [OpenJDK 17](https://adoptium.net/) yuklab oling
2. [Maven](https://maven.apache.org/download.cgi) yuklab oling
3. PATH ga qo'shing

#### Linux:
```bash
sudo apt install openjdk-17-jdk maven
```

### 2.4. Node.js o'rnating

#### Windows:
[Node.js](https://nodejs.org/) rasmiy saytidan yuklab oling

#### Linux:
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs
```

### 2.5. Python o'rnating

#### Windows:
[Python](https://www.python.org/downloads/) rasmiy saytidan yuklab oling

#### Linux:
```bash
sudo apt install python3 python3-pip
```

## 3. Loyihani sozlash

### 3.1. Konfiguratsiya faylini yarating
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

### 3.2. Majburiy sozlamalar

`application.properties` faylini tahrirlang:

```properties
# Database (o'z ma'lumotlaringizni kiriting)
spring.datasource.password=your_database_password

# JWT Secret (yangi secret yarating)
jwt.secret=your_new_jwt_secret_here
```

### 3.3. JWT Secret yaratish

#### Windows (PowerShell):
```powershell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

#### Linux/Mac:
```bash
openssl rand -base64 32
```

### 3.4. Ixtiyoriy sozlamalar

Email va OAuth2 xususiyatlari uchun:

```properties
# Email (password reset uchun)
spring.mail.username=your_email@gmail.com
spring.mail.password=your_gmail_app_password

# OAuth2 (social login uchun - o'z kalitlaringizni yarating)
spring.security.oauth2.client.registration.github.client-id=your_github_client_id
spring.security.oauth2.client.registration.github.client-secret=your_github_secret
```

## 4. Loyihani ishga tushirish

### 4.1. Dependencies o'rnatish
```bash
mvn clean install
```

### 4.2. Loyihani ishga tushirish
```bash
mvn spring-boot:run
```

### 4.3. Tekshirish
- **API**: http://localhost:8080/swagger-ui.html
- **Health**: http://localhost:8080/api/system/health

## 5. Test qilish

### 5.1. Test user bilan kirish
- **Username**: `testuser`
- **Password**: `test123`

### 5.2. Kod bajarish testi
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'
```

## 6. Muammolarni hal qilish

### 6.1. Port band bo'lsa
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <process_id> /F

# Linux
sudo lsof -ti:8080 | xargs sudo kill -9
```

### 6.2. Database connection xatosi
1. PostgreSQL service ishlab turganini tekshiring
2. Database yaratilganini tekshiring
3. Username/password to'g'riligini tekshiring

### 6.3. Code execution ishlamasa
Qaysi tillar ishlayotganini tekshiring:
```bash
# JavaScript
node --version

# Python
python --version
# yoki
python3 --version

# Java
java --version

# PHP (agar o'rnatgan bo'lsangiz)
php --version
```

## 7. Qo'shimcha tillar o'rnatish

### PHP (ixtiyoriy):
#### Windows:
1. [PHP](https://windows.php.net/download/) yuklab oling
2. `C:\php` ga extract qiling
3. PATH ga qo'shing

#### Linux:
```bash
sudo apt install php php-cli
```

### C++ (ixtiyoriy):
#### Windows:
1. [MSYS2](https://www.msys2.org/) o'rnating
2. MinGW-w64 o'rnating:
```bash
pacman -S mingw-w64-x86_64-gcc
```

#### Linux:
```bash
sudo apt install build-essential g++
```

## 8. Production uchun

Production muhitida qo'shimcha sozlamalar:

```properties
# Logging
logging.level.root=WARN
logging.level.com.code.algonix=INFO

# Security
server.address=127.0.0.1  # faqat local access
email.validation.enabled=true  # email validation yoqing
```

## Yordam

Muammolar bo'lsa:
1. README.md faylini o'qing
2. Logs'ni tekshiring
3. System requirements'ni tekshiring
4. GitHub Issues yarating