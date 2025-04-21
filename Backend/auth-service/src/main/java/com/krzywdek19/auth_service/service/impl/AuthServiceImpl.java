package com.krzywdek19.auth_service.service.impl;

import com.krzywdek19.auth_service.exception.AuthError;
import com.krzywdek19.auth_service.exception.AuthException;
import com.krzywdek19.auth_service.model.Role;
import com.krzywdek19.auth_service.model.User;
import com.krzywdek19.auth_service.model.VerificationToken;
import com.krzywdek19.auth_service.model.dto.RefreshTokenDto;
import com.krzywdek19.auth_service.model.dto.ResetPasswordDto;
import com.krzywdek19.auth_service.model.dto.SignInDto;
import com.krzywdek19.auth_service.model.dto.SignUpDto;
import com.krzywdek19.auth_service.repository.TokenRepository;
import com.krzywdek19.auth_service.repository.UserRepository;
import com.krzywdek19.auth_service.response.AuthResponseDto;
import com.krzywdek19.auth_service.service.AuthService;
import com.krzywdek19.auth_service.service.JwtService;
import com.krzywdek19.auth_service.service.RedisTokenService;
import com.krzywdek19.auth_service.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisTokenService redisTokenService;
    private final VerificationTokenService verificationTokenService;
    private final TokenRepository tokenRepository;

    @Override
    public void signUp(SignUpDto signUpDto) {
        if(userRepository.existsByEmail(signUpDto.email())){
            throw new AuthException(AuthError.EMAIL_IS_TAKEN);
        }
        if(userRepository.existsByUsername(signUpDto.username())){
            throw new AuthException(AuthError.USERNAME_IS_TAKEN);
        }
        var user = User
                .builder()
                .role(Role.USER)
                .email(signUpDto.email())
                .username(signUpDto.username())
                .password(passwordEncoder.encode(signUpDto.password()))
                .build();
        userRepository.save(user);
        verificationTokenService.sendVerificationToken(user);
    }

    @Override
    public AuthResponseDto signIn(SignInDto signInDto) {
        authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(signInDto.email(), signInDto.password())
                );

        var user =  userRepository.findByEmail(signInDto.email())
                .orElseThrow(() -> new AuthException(AuthError.USER_NOT_FOUND));
        String accessToken = jwtService.generateToken(user.getEmail(), user.getId(), List.of(user.getRole().name()));
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getId(), List.of(user.getRole().name()));
        redisTokenService.saveRefreshToken(user.getId().toString(), refreshToken, jwtService.extractExpiration(refreshToken));
        return new AuthResponseDto(accessToken, refreshToken);
    }

    @Override
    public String refreshToken(RefreshTokenDto refreshTokenDto) {
        String stored = redisTokenService.getRefreshToken(refreshTokenDto.userId().toString());
        if(stored == null || !stored.equals(refreshTokenDto.refreshToken())){
            throw new AuthException(AuthError.INVALID_REFRESH_TOKEN);
        }

        var user = userRepository.findById(refreshTokenDto.userId())
                .orElseThrow(() -> new AuthException(AuthError.USER_NOT_FOUND));

        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getId(), List.of(user.getRole().name()));
        redisTokenService.saveRefreshToken(user.getId().toString(),newRefreshToken, jwtService.extractExpiration(newRefreshToken));

        return newRefreshToken;
    }

    @Override
    @Transactional
    public void active(String verificationToken) {
           var user = verificationTokenService.getUserByToken(verificationToken);
           if(user.isActive()){
               throw new AuthException(AuthError.ACCOUNT_IS_ALREADY_ACTIVATED);
           }
           if(verificationTokenService.verifyToken(verificationToken)){
               user.activeAccount();
               userRepository.save(user);
               tokenRepository.deleteByUser(user);
           }else {
               throw new AuthException(AuthError.TOKEN_IS_INVALID);
           }
    }

    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        var user = userRepository.findByEmail(resetPasswordDto.email())
                .orElseThrow(() -> new AuthException(AuthError.USER_NOT_FOUND));
        if(passwordEncoder.matches(resetPasswordDto.oldPassword(), user.getPassword())){
            user.setPassword(passwordEncoder.encode(resetPasswordDto.newPassword()));
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPasswordByToken(String token, String newPassword) {
        if(verificationTokenService.verifyToken(token)) {
            var user = verificationTokenService
                    .getUserByToken(token);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            tokenRepository.deleteByUser(user);
        }else {
            throw new AuthException(AuthError.TOKEN_IS_INVALID);
        }
    }

    public void sendResetPasswordToken(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthError.USER_NOT_FOUND));
        verificationTokenService.sendResetPasswordToken(user);
    }

    public void sendActivationToken(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthError.USER_NOT_FOUND));
        verificationTokenService.sendVerificationToken(user);
    }
}
