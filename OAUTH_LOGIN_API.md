# OAuth Login API Documentation

GitHub va Google orqali login qilish uchun OAuth2 API'lari.

## Endpoints

### 1. GitHub Login

GitHub orqali login qilish.

#### URL
```
GET /oauth2/authorization/github
```

#### Authentication
- **Required**: No
- **Type**: OAuth2

#### Flow

1. Frontend'dan GitHub login tugmasini bosish
2. Backend'ga redirect: `http://localhost:8080/oauth2/authorization/github`
3. GitHub login sahifasiga yo'naltirish
4. Foydalanuvchi GitHub'da login qiladi
5. GitHub callback: `/login/oauth2/code/github`
6. Backend token yaratadi va frontend'ga redirect qiladi
7. Frontend callback: `http://localhost:4200/auth/callback?accessToken={token}&refreshToken={token}`

#### Configuration

**application.properties:**
```properties
spring.security.oauth2.client.registration.github.client-id=YOUR_GITHUB_CLIENT_ID
spring.security.oauth2.client.registration.github.client-secret=YOUR_GITHUB_CLIENT_SECRET
spring.security.oauth2.client.registration.github.scope=user:email,read:user
```

#### GitHub OAuth App Setup

1. GitHub Settings > Developer settings > OAuth Apps
2. New OAuth App
3. Application name: `Algonix`
4. Homepage URL: `http://localhost:8080`
5. Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
6. Copy Client ID and Client Secret

### 2. Google Login

Google orqali login qilish.

#### URL
```
GET /oauth2/authorization/google
```

#### Authentication
- **Required**: No
- **Type**: OAuth2

#### Flow

1. Frontend'dan Google login tugmasini bosish
2. Backend'ga redirect: `http://localhost:8080/oauth2/authorization/google`
3. Google login sahifasiga yo'naltirish
4. Foydalanuvchi Google'da login qiladi
5. Google callback: `/login/oauth2/code/google`
6. Backend token yaratadi va frontend'ga redirect qiladi
7. Frontend callback: `http://localhost:4200/auth/callback?accessToken={token}&refreshToken={token}`

#### Configuration

**application.properties:**
```properties
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=profile,email
```

#### Google OAuth Setup

1. Google Cloud Console: https://console.cloud.google.com/
2. Create Project: `Algonix`
3. APIs & Services > Credentials
4. Create OAuth 2.0 Client ID
5. Application type: Web application
6. Authorized redirect URIs: `http://localhost:8080/login/oauth2/code/google`
7. Copy Client ID and Client Secret

## Frontend Integration

### React Example

```jsx
import React from 'react';

function LoginPage() {
  const handleGitHubLogin = () => {
    // Redirect to GitHub OAuth
    window.location.href = 'http://localhost:8080/oauth2/authorization/github';
  };

  const handleGoogleLogin = () => {
    // Redirect to Google OAuth
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  return (
    <div className="login-page">
      <h2>Login to Algonix</h2>
      
      <button onClick={handleGitHubLogin} className="btn btn-github">
        <i className="fab fa-github"></i> Login with GitHub
      </button>
      
      <button onClick={handleGoogleLogin} className="btn btn-google">
        <i className="fab fa-google"></i> Login with Google
      </button>
    </div>
  );
}

export default LoginPage;
```

### Callback Handler

```jsx
import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

function AuthCallback() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const accessToken = searchParams.get('accessToken');
    const refreshToken = searchParams.get('refreshToken');

    if (accessToken && refreshToken) {
      // Save tokens to localStorage
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);

      // Redirect to dashboard
      navigate('/dashboard');
    } else {
      // Error handling
      console.error('OAuth login failed');
      navigate('/login');
    }
  }, [searchParams, navigate]);

  return (
    <div className="auth-callback">
      <p>Processing login...</p>
    </div>
  );
}

export default AuthCallback;
```

### Angular Example

```typescript
// login.component.ts
import { Component } from '@angular/core';

@Component({
  selector: 'app-login',
  template: `
    <div class="login-page">
      <h2>Login to Algonix</h2>
      
      <button (click)="loginWithGitHub()" class="btn btn-github">
        <i class="fab fa-github"></i> Login with GitHub
      </button>
      
      <button (click)="loginWithGoogle()" class="btn btn-google">
        <i class="fab fa-google"></i> Login with Google
      </button>
    </div>
  `
})
export class LoginComponent {
  loginWithGitHub() {
    window.location.href = 'http://localhost:8080/oauth2/authorization/github';
  }

  loginWithGoogle() {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  }
}
```

```typescript
// auth-callback.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-auth-callback',
  template: '<p>Processing login...</p>'
})
export class AuthCallbackComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const accessToken = params['accessToken'];
      const refreshToken = params['refreshToken'];

      if (accessToken && refreshToken) {
        // Save tokens
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);

        // Redirect to dashboard
        this.router.navigate(['/dashboard']);
      } else {
        // Error handling
        console.error('OAuth login failed');
        this.router.navigate(['/login']);
      }
    });
  }
}
```

### Vanilla JavaScript Example

```html
<!DOCTYPE html>
<html>
<head>
    <title>Login - Algonix</title>
</head>
<body>
    <div class="login-page">
        <h2>Login to Algonix</h2>
        
        <button onclick="loginWithGitHub()" class="btn btn-github">
            <i class="fab fa-github"></i> Login with GitHub
        </button>
        
        <button onclick="loginWithGoogle()" class="btn btn-google">
            <i class="fab fa-google"></i> Login with Google
        </button>
    </div>

    <script>
        function loginWithGitHub() {
            window.location.href = 'http://localhost:8080/oauth2/authorization/github';
        }

        function loginWithGoogle() {
            window.location.href = 'http://localhost:8080/oauth2/authorization/google';
        }

        // Callback handler (auth-callback.html)
        function handleOAuthCallback() {
            const urlParams = new URLSearchParams(window.location.search);
            const accessToken = urlParams.get('accessToken');
            const refreshToken = urlParams.get('refreshToken');

            if (accessToken && refreshToken) {
                // Save tokens
                localStorage.setItem('accessToken', accessToken);
                localStorage.setItem('refreshToken', refreshToken);

                // Redirect to dashboard
                window.location.href = '/dashboard.html';
            } else {
                // Error handling
                console.error('OAuth login failed');
                window.location.href = '/login.html';
            }
        }

        // Call on callback page
        if (window.location.pathname === '/auth/callback') {
            handleOAuthCallback();
        }
    </script>
</body>
</html>
```

## Backend Implementation

### OAuth2Config.java

```java
@Configuration
@RequiredArgsConstructor
@Slf4j
public class OAuth2Config {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final MessageService messageService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        
        return userRequest -> {
            OAuth2User oauth2User = delegate.loadUser(userRequest);
            
            String provider = userRequest.getClientRegistration().getRegistrationId();
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String login = oauth2User.getAttribute("login"); // GitHub username
            
            // Find or create user
            UserEntity user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        UserEntity newUser = UserEntity.builder()
                                .username(login != null ? login : email.split("@")[0])
                                .email(email)
                                .password("") // OAuth users don't have password
                                .role(Role.USER)
                                .build();
                        userRepository.save(newUser);
                        
                        // Welcome message
                        messageService.createWelcomeMessage(newUser);
                        
                        return newUser;
                    });
            
            return oauth2User;
        };
    }

    @Bean
    public AuthenticationSuccessHandler oauth2SuccessHandler() {
        return (request, response, authentication) -> {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String email = oauth2User.getAttribute("email");
            
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Generate JWT tokens
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            
            // Redirect to frontend with tokens
            String redirectUrl = String.format("%s/auth/callback?accessToken=%s&refreshToken=%s",
                    frontendUrl,
                    URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                    URLEncoder.encode(refreshToken, StandardCharsets.UTF_8));
            
            response.sendRedirect(redirectUrl);
        };
    }
}
```

## Security Configuration

### SecurityConfig.java

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    // OAuth2 endpoints
                    .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                    // Other endpoints...
                    .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                    .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService))
                    .successHandler(oauth2SuccessHandler)
            )
            .build();
}
```

## Response Format

### Success Redirect

```
http://localhost:4200/auth/callback?accessToken=eyJhbGci...&refreshToken=eyJhbGci...
```

### Tokens

**Access Token:**
- Type: JWT
- Expiration: 1 hour (3600000 ms)
- Use: API requests

**Refresh Token:**
- Type: JWT
- Expiration: 7 days (604800000 ms)
- Use: Token refresh

## Error Handling

### Common Errors

1. **Invalid Client ID/Secret**
   - Check application.properties configuration
   - Verify OAuth app settings

2. **Redirect URI Mismatch**
   - Ensure callback URL matches OAuth app configuration
   - Format: `http://localhost:8080/login/oauth2/code/{provider}`

3. **Scope Issues**
   - GitHub: `user:email,read:user`
   - Google: `profile,email`

4. **CORS Errors**
   - Configure CORS in SecurityConfig
   - Allow frontend origin

## Testing

### Manual Testing

1. Start backend: `mvn spring-boot:run`
2. Open browser: `http://localhost:8080/oauth2/authorization/github`
3. Login with GitHub/Google
4. Check redirect to frontend with tokens

### cURL Testing (Not recommended for OAuth)

OAuth requires browser interaction, use Postman or browser for testing.

## Production Deployment

### Environment Variables

```bash
# GitHub
GITHUB_CLIENT_ID=your_production_client_id
GITHUB_CLIENT_SECRET=your_production_client_secret

# Google
GOOGLE_CLIENT_ID=your_production_client_id
GOOGLE_CLIENT_SECRET=your_production_client_secret

# Frontend URL
FRONTEND_URL=https://yourdomain.com
```

### Update Callback URLs

**GitHub:**
- Development: `http://localhost:8080/login/oauth2/code/github`
- Production: `https://api.yourdomain.com/login/oauth2/code/github`

**Google:**
- Development: `http://localhost:8080/login/oauth2/code/google`
- Production: `https://api.yourdomain.com/login/oauth2/code/google`

## Notes

1. OAuth users don't have passwords (password field is empty)
2. Username is generated from GitHub login or email
3. Welcome message is sent to new OAuth users
4. Streak is checked and updated on login
5. Tokens are URL-encoded in redirect

## Related Documentation

- [GITHUB_OAUTH_SETUP.md](GITHUB_OAUTH_SETUP.md) - GitHub OAuth setup guide
- JWT token format and usage
- User authentication flow


## Token'ni API Request'larda Ishlatish

### React Example - API Service

```jsx
// services/api.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Axios instance yaratish
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - har bir request'ga token qo'shish
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - token expired bo'lsa refresh qilish
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Agar 401 error bo'lsa va retry qilinmagan bo'lsa
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // Refresh token bilan yangi access token olish
        const refreshToken = localStorage.getItem('refreshToken');
        const response = await axios.post(
          `${API_BASE_URL}/auth/refresh?refreshToken=${refreshToken}`
        );

        const { accessToken } = response.data;

        // Yangi token'ni saqlash
        localStorage.setItem('accessToken', accessToken);

        // Original request'ni yangi token bilan qayta yuborish
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh token ham expired bo'lsa, login sahifasiga yo'naltirish
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
```

### API Service'dan foydalanish

```jsx
// components/Profile.jsx
import React, { useEffect, useState } from 'react';
import api from '../services/api';

function Profile() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const response = await api.get('/profile/me');
      setProfile(response.data);
    } catch (error) {
      console.error('Failed to fetch profile:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>Loading...</div>;

  return (
    <div className="profile">
      <h2>Welcome, {profile?.username}!</h2>
      <p>Email: {profile?.email}</p>
      <p>Total Solved: {profile?.statistics?.totalSolved}</p>
    </div>
  );
}

export default Profile;
```

### Angular Example - HTTP Interceptor

```typescript
// services/auth.interceptor.ts
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, filter, take, switchMap } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Token qo'shish
    const token = localStorage.getItem('accessToken');
    if (token) {
      request = this.addToken(request, token);
    }

    return next.handle(request).pipe(
      catchError(error => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          return this.handle401Error(request, next);
        }
        return throwError(() => error);
      })
    );
  }

  private addToken(request: HttpRequest<any>, token: string) {
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      const refreshToken = localStorage.getItem('refreshToken');

      if (refreshToken) {
        return this.authService.refreshToken(refreshToken).pipe(
          switchMap((response: any) => {
            this.isRefreshing = false;
            localStorage.setItem('accessToken', response.accessToken);
            this.refreshTokenSubject.next(response.accessToken);
            return next.handle(this.addToken(request, response.accessToken));
          }),
          catchError((err) => {
            this.isRefreshing = false;
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            this.router.navigate(['/login']);
            return throwError(() => err);
          })
        );
      }
    }

    return this.refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap((token) => next.handle(this.addToken(request, token)))
    );
  }
}
```

```typescript
// services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  refreshToken(refreshToken: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/refresh?refreshToken=${refreshToken}`, {});
  }

  logout() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('accessToken');
  }
}
```

```typescript
// app.module.ts
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './services/auth.interceptor';

@NgModule({
  // ...
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ]
})
export class AppModule { }
```

### Vanilla JavaScript Example

```javascript
// api.js
class ApiService {
  constructor() {
    this.baseURL = 'http://localhost:8080/api';
  }

  async request(endpoint, options = {}) {
    const token = localStorage.getItem('accessToken');
    
    const config = {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': `Bearer ${token}` }),
        ...options.headers,
      },
    };

    try {
      const response = await fetch(`${this.baseURL}${endpoint}`, config);

      // Token expired bo'lsa
      if (response.status === 401) {
        const refreshed = await this.refreshToken();
        if (refreshed) {
          // Retry original request
          const retryToken = localStorage.getItem('accessToken');
          config.headers['Authorization'] = `Bearer ${retryToken}`;
          return await fetch(`${this.baseURL}${endpoint}`, config);
        } else {
          // Refresh failed, redirect to login
          window.location.href = '/login.html';
          throw new Error('Authentication failed');
        }
      }

      return await response.json();
    } catch (error) {
      console.error('API request failed:', error);
      throw error;
    }
  }

  async refreshToken() {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) return false;

    try {
      const response = await fetch(
        `${this.baseURL}/auth/refresh?refreshToken=${refreshToken}`,
        { method: 'POST' }
      );

      if (response.ok) {
        const data = await response.json();
        localStorage.setItem('accessToken', data.accessToken);
        return true;
      }
      return false;
    } catch (error) {
      return false;
    }
  }

  async get(endpoint) {
    return this.request(endpoint, { method: 'GET' });
  }

  async post(endpoint, data) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async put(endpoint, data) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  }
}

// Export singleton instance
const api = new ApiService();
```

```javascript
// Usage example
async function loadProfile() {
  try {
    const profile = await api.get('/profile/me');
    document.getElementById('username').textContent = profile.username;
    document.getElementById('email').textContent = profile.email;
  } catch (error) {
    console.error('Failed to load profile:', error);
  }
}

// Call on page load
document.addEventListener('DOMContentLoaded', () => {
  if (localStorage.getItem('accessToken')) {
    loadProfile();
  } else {
    window.location.href = '/login.html';
  }
});
```

## Route Protection

### React - Protected Route

```jsx
// components/ProtectedRoute.jsx
import { Navigate } from 'react-router-dom';

function ProtectedRoute({ children }) {
  const token = localStorage.getItem('accessToken');

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

export default ProtectedRoute;
```

```jsx
// App.jsx
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import AuthCallback from './pages/AuthCallback';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/auth/callback" element={<AuthCallback />} />
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
```

### Angular - Auth Guard

```typescript
// guards/auth.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    if (this.authService.isLoggedIn()) {
      return true;
    }

    this.router.navigate(['/login']);
    return false;
  }
}
```

```typescript
// app-routing.module.ts
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'auth/callback', component: AuthCallbackComponent },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard]
  }
];
```

## Summary

1. **OAuth Login** → Backend redirect qiladi → Frontend `/auth/callback?accessToken=...&refreshToken=...`
2. **Frontend** → Token'larni localStorage'ga saqlaydi
3. **API Requests** → Har bir request'ga `Authorization: Bearer {token}` header qo'shiladi
4. **Token Expired** → Refresh token bilan yangi access token olinadi
5. **Refresh Failed** → Login sahifasiga redirect qilinadi
