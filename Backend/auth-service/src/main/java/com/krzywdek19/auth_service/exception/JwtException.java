package com.krzywdek19.auth_service.exception;

import lombok.Getter;

@Getter
public class JwtException extends RuntimeException {
    private final JwtError jwtError;
    public JwtException(JwtError jwtError) {
        super(jwtError.getMessage());
        this.jwtError = jwtError;
    }
}
