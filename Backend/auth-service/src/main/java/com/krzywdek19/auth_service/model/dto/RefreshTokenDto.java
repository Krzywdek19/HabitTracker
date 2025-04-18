package com.krzywdek19.auth_service.model.dto;

public record RefreshTokenDto(Long userId, String refreshToken) {
}
