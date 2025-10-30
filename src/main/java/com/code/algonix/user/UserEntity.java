package com.code.algonix.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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

    private static final String GENERATOR_NAME = "users_gen";
    private static final String SEQUENCE_NAME = "users_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = GENERATOR_NAME)
    @SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Long id;

    private String username;
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

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
}
