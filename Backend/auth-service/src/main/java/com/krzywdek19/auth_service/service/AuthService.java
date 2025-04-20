package com.krzywdek19.auth_service.service;

import com.krzywdek19.auth_service.model.User;
import com.krzywdek19.auth_service.model.VerificationToken;
import com.krzywdek19.auth_service.model.dto.RefreshTokenDto;
import com.krzywdek19.auth_service.model.dto.ResetPasswordDto;
import com.krzywdek19.auth_service.model.dto.SignInDto;
import com.krzywdek19.auth_service.model.dto.SignUpDto;
import com.krzywdek19.auth_service.response.AuthResponseDto;

public interface AuthService {
    void signUp(SignUpDto signUpDto);
    AuthResponseDto signIn(SignInDto signInDto);
    String refreshToken(RefreshTokenDto refreshTokenDto);
    void active(User user, String verificationToken);
    void resetPassword(ResetPasswordDto resetPasswordDto);
    void resetPasswordByToken(String verificationToken, Long userId, String newPassword);
}
