package com.krzywdek19.auth_service.service.impl;

import com.krzywdek19.auth_service.exception.AuthError;
import com.krzywdek19.auth_service.exception.AuthException;
import com.krzywdek19.auth_service.model.User;
import com.krzywdek19.auth_service.model.VerificationToken;
import com.krzywdek19.auth_service.repository.TokenRepository;
import com.krzywdek19.auth_service.repository.UserRepository;
import com.krzywdek19.auth_service.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {
    private final RabbitTemplate rabbitTemplate;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Override
    public void sendVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken;
        if(tokenRepository.existsByUser(user)){
            tokenRepository.deleteByUser(user);
        }
        verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        tokenRepository.save(verificationToken);
        rabbitTemplate.convertAndSend("auth_service_activation", verificationToken.getToken());
    }

    @Override
    public String getVerificationToken(User user) {
        return tokenRepository.findByUser(user)
                .orElseThrow(()-> new AuthException(AuthError.TOKEN_NOT_FOUND))
                .getToken();
    }

    @Override
    public boolean verifyToken(Long userId, String token) {
        var verificationToken = tokenRepository.findByUserId(userId)
                .orElseThrow(()-> new AuthException(AuthError.TOKEN_NOT_FOUND));
        if(verificationToken.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new AuthException(AuthError.TOKEN_IS_EXPIRED);
        }
        return token != null && verificationToken
                .getToken()
                .trim()
                .equals(token.trim());
    }
}
