package com.krzywdek19.auth_service.service;


import io.jsonwebtoken.Claims;

import java.util.List;

public interface JwtService {
    String generateToken(String email, Long userId, List<String> roles);
    String generateRefreshToken(String email, Long userId, List<String> roles);
    Claims extractClaims(String token);
    Long extractExpiration(String token);
    Long extractId(String token);
    List<String> extractRoles(String token);
    String extractEmail(String token);
    boolean isTokenValid(String token, String email);
}
