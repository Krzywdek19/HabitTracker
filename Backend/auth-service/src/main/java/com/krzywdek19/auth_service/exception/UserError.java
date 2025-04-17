package com.krzywdek19.auth_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserError {
    USER_NOT_FOUND("User with this email doesn't exist");
    private String message;
}
