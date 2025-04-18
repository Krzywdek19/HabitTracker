package com.krzywdek19.auth_service.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpDto(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, message = "Username must be at least 3 characters long")
        String username,

        @Email(message = "Invalid email address")
        @NotBlank(message = "Email cannot be blank")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password
) {}

//todo improve validation