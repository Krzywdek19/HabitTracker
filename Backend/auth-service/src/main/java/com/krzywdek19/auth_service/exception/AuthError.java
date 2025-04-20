package com.krzywdek19.auth_service.exception;

import lombok.Getter;

@Getter
public enum AuthError {
    EMAIL_IS_TAKEN("Email is taken!"),
    USERNAME_IS_TAKEN("Username is taken!"),
    USER_NOT_FOUND("User not found!"),
    INVALID_REFRESH_TOKEN("Invalid refresh token!"),
    ACCOUNT_IS_EXPIRED("Account is expired!"),
    ACCOUNT_IS_NOT_ACTIVATED("Account is not activated!"),
    TOKEN_NOT_FOUND("Token not found!"),
    ACCOUNT_IS_ALREADY_ACTIVATED("Account is already activated!"),
    TOKEN_IS_INVALID("Token is invalid!"),
    TOKEN_IS_EXPIRED("Token is expired!"),;


    AuthError(String message) {
        this.message = message;
    }

    private String message;
}
