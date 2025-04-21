package com.krzywdek19.auth_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Username cannot be blank")
    @Column(nullable = false, unique = true)
    private String username;
    @NotBlank(message = "Email cannot be blank")
    @Column(nullable = false, unique = true)
    @Email(message = "Provided email is incorrect")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    private String password;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private boolean isActive = false;

    @Override
    public boolean isAccountNonExpired() {
        return lastLogin != null && LocalDateTime.now().minusDays(180).isBefore(lastLogin) ||
                createdAt != null && LocalDateTime.now().minusDays(180).isBefore(createdAt);
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive || LocalDateTime.now().minusDays(2).isBefore(createdAt);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return email;
    }

    public void activeAccount(){
        this.isActive = true;
    }

    @PrePersist
    private void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

}
