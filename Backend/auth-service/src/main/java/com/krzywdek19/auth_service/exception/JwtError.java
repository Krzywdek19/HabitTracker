package com.krzywdek19.auth_service.exception;

import lombok.Getter;

@Getter
public enum JwtError {
    INVALID_ID_FORMAT("User's ID saved on JWT is invalid!"),
    INVALID_TOKEN("Provided token is invalid!"),;

    JwtError(String message) {
        this.message = message;
    }

    private String message;
}
