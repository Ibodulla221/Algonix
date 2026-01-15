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
