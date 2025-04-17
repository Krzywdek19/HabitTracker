package com.krzywdek19.auth_service.service.impl;

import com.krzywdek19.auth_service.exception.JwtError;
import com.krzywdek19.auth_service.exception.JwtException;
import com.krzywdek19.auth_service.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class JwtServiceImpl implements JwtService {
    private final SignatureAlgorithm algorithm;
    private final KeyPair keyPair;
    @Value("${jwt.iss}")
    private String issuer;
    @Value("${jwt.expiration}")
    private Long tokenExpiration;
    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpiration;

    JwtServiceImpl(KeyPair keys) {
        keyPair = keys;
        algorithm = SignatureAlgorithm.RS256;
    }

    private JwtBuilder buildToken(String email, Long userId, List<String> roles) {
        return Jwts.builder()
                .claim("userId",userId)
                .claim("roles",roles)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setIssuer(issuer)
                .signWith(keyPair.getPrivate(), algorithm);
    }

    @Override
    public String generateToken(String email, Long userId, List<String> roles) {
        return buildToken(email,userId,roles).setExpiration(new Date(System.currentTimeMillis() + tokenExpiration)).compact();
    }

    @Override
    public String generateRefreshToken(String email, Long userId, List<String> roles) {
        return buildToken(email,userId,roles).setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration)).compact();
    }

    @Override
    public Claims extractClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(keyPair.getPublic())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (JwtException | IllegalArgumentException e) {
            throw new JwtException(JwtError.INVALID_TOKEN);
        }
    }

    @Override
    public Long extractId(String token) {
        try {
            Number id = extractClaims(token).get("userId", Number.class);
            return id.longValue();
        }catch (Exception e) {
            throw new JwtException(JwtError.INVALID_ID_FORMAT);
        }
    }

    @Override
    public List<String> extractRoles(String token) {
        List<?> roles = extractClaims(token).get("roles",List.class);
        return roles.stream().map(String.class::cast).collect(Collectors.toList());
    }

    @Override
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    @Override
    public boolean isTokenValid(String token, String email) {
        return !isTokenExpired(token) && isTokenEmailValid(token, email);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private boolean isTokenEmailValid(String token, String email) {
        return extractEmail(token).equals(email);
    }
}
