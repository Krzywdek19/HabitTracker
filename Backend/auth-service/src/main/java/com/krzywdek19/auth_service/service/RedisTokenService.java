package com.krzywdek19.auth_service.service;

public interface RedisTokenService {
    void saveRefreshToken(String userId, String refreshToken, long expirationMillis);
    String getRefreshToken(String userId);
    void deleteRefreshToken(String userId);
}
