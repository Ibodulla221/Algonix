package com.code.algonix.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
@Getter
@Setter
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    private Long id;

    private String username;
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Profile fields
    private String firstName;
    private String lastName;
    private String bio; // Qisqa tavsif
    private String location; // Mamlakat/shahar
    private String company; // Kompaniya
    private String jobTitle; // Lavozim
    private String website; // Shaxsiy website
    private String githubUsername; // GitHub username
    private String linkedinUrl; // LinkedIn profil
    private String twitterUsername; // Twitter username
    
    // Avatar
    private String avatarUrl; // Avatar rasm URL'i
    private String avatarFileName; // Fayl nomi
    
    // Settings
    @Builder.Default
    private Boolean isProfilePublic = true; // Profil ochiq/yopiq
    @Builder.Default
    private Boolean showEmail = false; // Email ko'rsatish
    @Builder.Default
    private Boolean showLocation = true; // Joylashuvni ko'rsatish
    @Builder.Default
    private Boolean showCompany = true; // Kompaniyani ko'rsatish
    
    // Timestamps
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatistics statistics;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
    
    // Helper methods
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return username;
    }
    
    public String getDisplayName() {
        String fullName = getFullName();
        return fullName.equals(username) ? username : fullName;
    }
}
