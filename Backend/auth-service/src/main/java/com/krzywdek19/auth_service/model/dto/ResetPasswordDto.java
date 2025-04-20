package com.krzywdek19.auth_service.model.dto;

public record ResetPasswordDto(String email, String oldPassword, String newPassword) {
}
