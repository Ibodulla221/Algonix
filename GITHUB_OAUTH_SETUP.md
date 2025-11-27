# GitHub OAuth Setup - Yo'riqnoma

## 🔐 GitHub OAuth App yaratish

### 1-QADAM: GitHub Developer Settings

1. **GitHub'ga kiring:** https://github.com
2. **Settings'ga o'ting:**
   - O'ng yuqoridagi profil rasmi → **Settings**
3. **Developer settings'ga o'ting:**
   - Chap menuda pastga scroll qiling
   - **Developer settings** ni bosing
   - Yoki to'g'ridan-to'g'ri: https://github.com/settings/developers

### 2-QADAM: OAuth App yaratish

1. **OAuth Apps'ga o'ting:**
   - Chap menuda **OAuth Apps** ni bosing
   - Yoki: https://github.com/settings/developers

2. **New OAuth App tugmasini bosing**

3. **Ma'lumotlarni to'ldiring:**

```
Application name: Algonix
Homepage URL: http://localhost:8080
Application description: Algonix - Coding Platform
Authorization callback URL: http://localhost:8080/login/oauth2/code/github
```

**Muhim:** Authorization callback URL to'g'ri bo'lishi kerak!

4. **Register application tugmasini bosing**

### 3-QADAM: Client ID va Client Secret olish

1. **Client ID:**
   - Yaratilgan OAuth App sahifasida ko'rsatiladi
   - Masalan: `Ov23liAbc123XyZ`
   - Ko'chirib oling

2. **Client Secret:**
   - **Generate a new client secret** tugmasini bosing
   - Secret paydo bo'ladi (faqat bir marta ko'rsatiladi!)
   - Masalan: `1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s0t`
   - **Darhol ko'chirib oling!**

### 4-QADAM: Application.properties'ga qo'yish

`src/main/resources/application.properties` faylini oching:

```properties
# OAuth2 - GitHub
spring.security.oauth2.client.registration.github.client-id=Ov23liAbc123XyZ
spring.security.oauth2.client.registration.github.client-secret=1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s0t
spring.security.oauth2.client.registration.github.scope=user:email,read:user
```

## 🚀 Qanday ishlaydi?

### Backend (Spring Boot)

1. User frontend'da "Login with GitHub" tugmasini bosadi
2. Frontend user'ni redirect qiladi: `http://localhost:8080/oauth2/authorization/github`
3. GitHub login sahifasi ochiladi
4. User GitHub'da login qiladi va ruxsat beradi
5. GitHub callback qiladi: `http://localhost:8080/login/oauth2/code/github?code=...`
6. Backend GitHub'dan user ma'lumotlarini oladi
7. Backend database'da user yaratadi yoki topadi
8. Backend JWT token yaratadi
9. Backend frontend'ga redirect qiladi: `http://localhost:4200/auth/callback?accessToken=...&refreshToken=...`
10. Frontend token'larni saqlaydi va user login qilgan

### Frontend (Angular/React)

**Login tugmasi:**
```html
<a href="http://localhost:8080/oauth2/authorization/github">
  <button>Login with GitHub</button>
</a>
```

**Callback sahifasi** (`/auth/callback`):
```typescript
// URL'dan token'larni olish
const params = new URLSearchParams(window.location.search);
const accessToken = params.get('accessToken');
const refreshToken = params.get('refreshToken');

// Token'larni saqlash
localStorage.setItem('accessToken', accessToken);
localStorage.setItem('refreshToken', refreshToken);

// Dashboard'ga redirect
router.navigate(['/dashboard']);
```

## 🧪 Test qilish

### 1. Loyihani ishga tushiring:
```bash
./mvnw.cmd spring-boot:run
```

### 2. Brauzerda oching:
```
http://localhost:8080/oauth2/authorization/github
```

### 3. GitHub login sahifasi ochiladi
- GitHub username/password kiriting
- "Authorize" tugmasini bosing

### 4. Redirect bo'ladi:
```
http://localhost:4200/auth/callback?accessToken=eyJhbGc...&refreshToken=eyJhbGc...
```

## 📋 Callback URL'lar

### Development:
```
Authorization callback URL: http://localhost:8080/login/oauth2/code/github
Frontend redirect URL: http://localhost:4200/auth/callback
```

### Production:
```
Authorization callback URL: https://api.algonix.com/login/oauth2/code/github
Frontend redirect URL: https://algonix.com/auth/callback
```

**Muhim:** Production'ga chiqishda GitHub OAuth App settings'da callback URL'ni yangilash kerak!

## ❓ Muammolar va yechimlar

### "The redirect_uri MUST match the registered callback URL"
**Sabab:** Callback URL noto'g'ri

**Yechim:**
- GitHub OAuth App settings'da callback URL'ni tekshiring
- To'g'ri format: `http://localhost:8080/login/oauth2/code/github`
- Port raqamini tekshiring (8080)

### "Bad credentials" yoki "401 Unauthorized"
**Sabab:** Client ID yoki Client Secret noto'g'ri

**Yechim:**
- `application.properties` faylida client-id va client-secret'ni tekshiring
- GitHub'da yangi Client Secret yarating
- Copy-paste qiling (qo'lda yozmaslik)

### User email null
**Sabab:** GitHub'da email private

**Yechim:**
- GitHub Settings → Emails
- "Keep my email addresses private" ni o'chiring
- Yoki public email tanlang

### Frontend'ga redirect bo'lmayapti
**Sabab:** `app.frontend.url` noto'g'ri

**Yechim:**
- `application.properties` da `app.frontend.url=http://localhost:4200` tekshiring
- Frontend ishlab turganini tekshiring

## 🔒 Xavfsizlik

1. **Client Secret'ni git'ga commit qilmang!**
   - `.gitignore` da `application.properties` bor
   - Environment variable ishlatish yaxshiroq

2. **Production'da HTTPS ishlatish SHART!**
   - GitHub OAuth faqat HTTPS callback URL'larni qabul qiladi (localhost bundan mustasno)

3. **Scope'ni minimal qiling:**
   - Faqat kerakli ma'lumotlarni so'rang
   - `user:email,read:user` - email va basic info

## 📊 Qo'shimcha ma'lumotlar

**GitHub OAuth Documentation:**
https://docs.github.com/en/apps/oauth-apps/building-oauth-apps

**Spring Security OAuth2 Documentation:**
https://docs.spring.io/spring-security/reference/servlet/oauth2/login/core.html

---

**Eslatma:** Email bilan login ham ishlayveradi. GitHub OAuth - bu qo'shimcha variant.
