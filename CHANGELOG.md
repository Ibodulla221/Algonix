# Changelog

## [2.0.0] - 2024-11-27

### ✨ Yangi xususiyatlar

#### Kod bajarish tizimi
- ✅ Native ProcessBuilder orqali xavfsiz kod bajarish
- ✅ 18 ta dasturlash tilini qo'llab-quvvatlash (Java, Python, C++, JavaScript, va boshqalar)
- ✅ Test case'larni avtomatik tekshirish
- ✅ Compile va runtime xatolarini aniqlash
- ✅ Timeout va memory limit
- ✅ `/api/problems/{id}/run` endpoint - kod test qilish
- ✅ Submission'lar uchun haqiqiy kod bajarish

#### Validation
- ✅ Input ma'lumotlarni tekshirish
- ✅ Email format validatsiyasi
- ✅ Password strength tekshirish (minimum 6 ta belgi)
- ✅ Username pattern validatsiyasi (faqat harf, raqam, _)
- ✅ `@Valid` annotation'lar qo'shildi

#### Error Handling
- ✅ Global exception handler
- ✅ To'g'ri error response'lar (ErrorResponse, ValidationErrorResponse)
- ✅ User-friendly xato xabarlari
- ✅ HTTP status code'lar to'g'ri qaytariladi

#### Logging
- ✅ SLF4J/Logback integration
- ✅ Console va file logging
- ✅ Log rotation (7 kun)
- ✅ Barcha service'larda logging qo'shildi

#### Security
- ✅ Environment variable'lar qo'llab-quvvatlash
- ✅ `application.properties.example` yaratildi
- ✅ Sensitive ma'lumotlar `.gitignore`ga qo'shildi
- ✅ Frontend URL configuration
- ✅ Password reset link dynamic

#### Testing
- ✅ Unit test sample'lar yaratildi
- ✅ `CodeExecutionServiceTest`
- ✅ `AuthServiceTest`
- ✅ Mockito integration

#### Documentation
- ✅ Swagger documentation yaxshilandi
- ✅ API versiya 2.0
- ✅ Qo'llab-quvvatlanadigan tillar ro'yxati
- ✅ Test user credentials
- ✅ README yangilandi

### 🔧 Tuzatishlar

#### DataInitializer
- ✅ To'g'ri email address'lar
- ✅ Default admin va test user
- ✅ Lombok `@RequiredArgsConstructor` ishlatildi
- ✅ Logging qo'shildi

#### UserEntity
- ✅ Database generation strategy tuzatildi
- ✅ `IDENTITY` va `SEQUENCE` konflikti hal qilindi

#### AuthService
- ✅ Frontend URL environment variable'dan olinadi
- ✅ Hardcoded IP address o'chirildi
- ✅ `@Value` annotation qo'shildi

#### ProblemService
- ✅ Code execution integration
- ✅ `runCode` metodi implement qilindi
- ✅ Service separation (ProblemServiceRunCode)

#### SubmissionService
- ✅ Haqiqiy kod bajarish
- ✅ Test result mapping
- ✅ Status calculation
- ✅ Error handling

### 📝 Configuration

#### application.properties
- ✅ `spring.jpa.show-sql=false` (production ready)
- ✅ `app.frontend.url` qo'shildi
- ✅ `code.execution.*` settings qo'shildi
- ✅ Comment'lar yaxshilandi

#### .gitignore
- ✅ `application.properties` qo'shildi
- ✅ Log fayllar qo'shildi
- ✅ Temporary fayllar qo'shildi

### 🗑️ O'chirilgan

- ❌ Hardcoded parollar va email'lar
- ❌ Mock kod bajarish
- ❌ `System.out.println` (logging bilan almashtirildi)
- ❌ Noto'g'ri database generation strategy

### 📊 Statistika

- **Yangi fayllar:** 8
- **O'zgartirilgan fayllar:** 15+
- **Qo'shilgan kod:** ~1500 qator
- **Test coverage:** Unit test'lar qo'shildi

### ⚠️ Breaking Changes

- `DataInitializer` - yangi default user credentials
- `application.properties` - yangi required field'lar
- API response format'lari o'zgardi (error handling)

### 🚀 Keyingi versiya uchun

- [ ] Async kod bajarish (CompletableFuture)
- [ ] Redis cache integration
- [ ] Rate limiting
- [ ] WebSocket (real-time submission updates)
- [ ] Problem difficulty recommendation
- [ ] User statistics dashboard
- [ ] Code plagiarism detection
- [ ] Discussion forum
- [ ] Editorial solutions

---

## [1.0.0] - 2024-11-26

### Initial Release
- Basic authentication (JWT)
- Problem CRUD operations
- Submission tracking
- PostgreSQL integration
- Swagger documentation
