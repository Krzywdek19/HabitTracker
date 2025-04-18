package com.krzywdek19.auth_service.response;

public record AuthResponseDto(
        String accessToken,
        String refreshToken
) {}