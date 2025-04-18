package com.krzywdek19.auth_service.service;

import com.krzywdek19.auth_service.model.dto.RefreshTokenDto;
import com.krzywdek19.auth_service.model.dto.SignInDto;
import com.krzywdek19.auth_service.model.dto.SignUpDto;
import com.krzywdek19.auth_service.response.AuthResponseDto;

public interface AuthService {
    void signUp(SignUpDto signUpDto);
    AuthResponseDto signIn(SignInDto signInDto);
    String refreshToken(RefreshTokenDto refreshTokenDto);
}
