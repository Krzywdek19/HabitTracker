package com.krzywdek19.auth_service.service.impl;

import com.krzywdek19.auth_service.service.RedisTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenServiceImpl implements RedisTokenService {
    private final RedisTemplate<String,String> redisTemplate;

    @Override
    public void saveRefreshToken(String userId, String refreshToken, long expirationMillis) {
        redisTemplate.opsForValue().set("refresh_token:" + userId, refreshToken, expirationMillis);
    }

    @Override
    public String getRefreshToken(String userId) {
        return redisTemplate.opsForValue().get("refresh_token:" + userId);
    }

    @Override
    public void deleteRefreshToken(String userId) {
        redisTemplate.delete("refresh_token:" + userId);
    }
}
