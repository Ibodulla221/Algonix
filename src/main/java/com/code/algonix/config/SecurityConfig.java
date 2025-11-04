package com.code.algonix.config;

import com.code.algonix.user.UserRepository;
import com.code.algonix.user.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults()) // Enable CORS
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/auth/me"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",
                                "/api/save-movie/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/save-movie/**",
                                "/api/movie/**",
                                "/api/episode/**",
                                "/api/comment/{id}",
                                "/api/comment/{count}",
                                "/api/minio/movie/**",
                                "/api/minio/stream/**",
                                "/api/minio/proxy/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/movie/{id}/views", "/api/episode/view/{id}").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/comment/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/comment/**", "/api/like/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/comment/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/auth/change-password").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/episode/view/**").hasAnyRole("USER","ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "/api/movie/**/views").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/movie/**", "/api/episode/**", "/api/minio/upload").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/movie/**", "/api/episode/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/movie/**", "/api/episode/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}