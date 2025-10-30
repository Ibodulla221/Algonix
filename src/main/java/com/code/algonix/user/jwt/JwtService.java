package com.code.algonix.user.jwt;

import com.code.algonix.user.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    private final String secret = "2lVo5TcjgTco3dUIwmYEIWcLfeiBR7QgjS9fFyn1Jdg="; // 256-bit for HS256
    private final long accessTokenExpiration = 1000 * 60 * 60; // 60 min
    private final long refreshTokenExpiration = 1000L * 60 * 60 * 24 * 7; // 7 days

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("username", user.getUsername());
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());

        Date expirationDate = new Date(System.currentTimeMillis() + accessTokenExpiration);
        claims.put("accessTokenExpiresAt", expirationDate.getTime()); // Add custom expiration timestamp

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateSimpleToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());

        Date expirationDate = new Date(System.currentTimeMillis() + accessTokenExpiration);
        claims.put("accessTokenExpiresAt", expirationDate.getTime()); // Add custom expiration timestamp

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserEntity user) {
        Date refreshExpirationDate = new Date(System.currentTimeMillis() + refreshTokenExpiration);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("refreshTokenExpiresAt", refreshExpirationDate.getTime()) // Custom claim
                .setIssuedAt(new Date())
                .setExpiration(refreshExpirationDate) // Standard `exp` field
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserEntity user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
