package com.krzywdek19.auth_service.repository;

import com.krzywdek19.auth_service.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(@NotBlank(message = "Email cannot be blank") @Email(message = "Provided email is incorrect") String email);
    boolean existsByEmail(@NotBlank(message = "Email cannot be blank") String email);
    boolean existsByUsername(@NotBlank(message = "Username cannot be blank") String username);
}
