package com.krzywdek19.auth_service.service;

import com.krzywdek19.auth_service.model.User;
import com.krzywdek19.auth_service.model.VerificationToken;

public interface VerificationTokenService {
    void sendVerificationToken(User user);
    void sendResetPasswordToken(User user);
    String getVerificationToken(User user);
    boolean verifyToken(String token);
    User getUserByToken(String token);
}
