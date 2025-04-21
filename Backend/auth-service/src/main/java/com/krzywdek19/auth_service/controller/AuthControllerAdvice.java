package com.krzywdek19.auth_service.controller;

import com.krzywdek19.auth_service.exception.AuthError;
import com.krzywdek19.auth_service.exception.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AuthControllerAdvice {
    private static final Map<AuthError, HttpStatus> errorStatusMap = new HashMap<>();

    static {
        errorStatusMap.put(AuthError.EMAIL_IS_TAKEN, HttpStatus.CONFLICT);
        errorStatusMap.put(AuthError.USERNAME_IS_TAKEN, HttpStatus.CONFLICT);
        errorStatusMap.put(AuthError.ACCOUNT_IS_ALREADY_ACTIVATED, HttpStatus.CONFLICT);

        errorStatusMap.put(AuthError.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        errorStatusMap.put(AuthError.TOKEN_NOT_FOUND, HttpStatus.NOT_FOUND);

        errorStatusMap.put(AuthError.ACCOUNT_IS_EXPIRED, HttpStatus.FORBIDDEN);
        errorStatusMap.put(AuthError.ACCOUNT_IS_NOT_ACTIVATED, HttpStatus.FORBIDDEN);

        errorStatusMap.put(AuthError.TOKEN_IS_INVALID, HttpStatus.UNAUTHORIZED);
        errorStatusMap.put(AuthError.TOKEN_IS_EXPIRED, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<?> handleAuthException(AuthException e) {
        AuthError error = e.getAuthError();
        HttpStatus status = errorStatusMap.getOrDefault(error, HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(status).body(error.getMessage());
    }
}