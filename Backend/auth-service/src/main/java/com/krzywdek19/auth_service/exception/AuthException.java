package com.krzywdek19.auth_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class AuthException extends RuntimeException {
    private final AuthError authError;
    public AuthException(AuthError error) {
        super(error.getMessage());
        this.authError = error;
    }
}
