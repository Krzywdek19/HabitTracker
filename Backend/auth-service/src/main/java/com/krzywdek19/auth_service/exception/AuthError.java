package com.krzywdek19.auth_service.exception;

import lombok.Getter;

@Getter
public enum AuthError {
    EMAIL_IS_TAKEN("Email is taken!"),
    USERNAME_IS_TAKEN("Username is taken!"),
    USER_NOT_FOUND("User not found!"),
    INVALID_REFRESH_TOKEN("Invalid refresh token!"),;


    AuthError(String message) {
        this.message = message;
    }

    private String message;
}
