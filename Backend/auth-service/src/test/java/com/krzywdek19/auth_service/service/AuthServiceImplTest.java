package com.krzywdek19.auth_service.service;


import com.krzywdek19.auth_service.exception.AuthError;
import com.krzywdek19.auth_service.exception.AuthException;
import com.krzywdek19.auth_service.model.Role;
import com.krzywdek19.auth_service.model.User;
import com.krzywdek19.auth_service.model.dto.RefreshTokenDto;
import com.krzywdek19.auth_service.model.dto.SignInDto;
import com.krzywdek19.auth_service.model.dto.SignUpDto;
import com.krzywdek19.auth_service.repository.UserRepository;
import com.krzywdek19.auth_service.response.AuthResponseDto;
import com.krzywdek19.auth_service.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceImplTest {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;
    AuthenticationManager authenticationManager;
    RedisTokenService redisTokenService;
    AuthService authService;
    SignUpDto signUpDto;
    SignInDto signInDto;

    String testEmail = "test@test.com";
    String testUsername = "test";
    String testPassword = "password123";

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);
        redisTokenService = mock(RedisTokenService.class);
        signUpDto = new SignUpDto(testUsername, testEmail, testPassword);
        signInDto = new SignInDto(testEmail, testPassword);

        authService = new AuthServiceImpl(
                userRepository, passwordEncoder, jwtService, authenticationManager, redisTokenService
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailTaken(){
        when(userRepository.existsByEmail(testEmail)).thenReturn(true);

        AuthException exception = assertThrows(AuthException.class, () -> authService.signUp(signUpDto));
        assertEquals(AuthError.EMAIL_IS_TAKEN, exception.getAuthError());
    }

    @Test
    void shouldThrowExceptionWhenUsernameTaken(){
        when(userRepository.existsByUsername(testUsername)).thenReturn(true);

        AuthException exception = assertThrows(AuthException.class, () -> authService.signUp(signUpDto));
        assertEquals(AuthError.USERNAME_IS_TAKEN, exception.getAuthError());
    }

    @Test
    void shouldCreateUserWhenSignUpSuccessful(){
        when(userRepository.existsByUsername(testUsername)).thenReturn(false);
        when(userRepository.existsByEmail(testEmail)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User());

        authService.signUp(signUpDto);

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(testPassword);
    }

    @Test
    void shouldReturnTokenWhenSignInSuccessful() {
        AuthResponseDto responseDto;

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(User.builder()
                .id(1L)
                .email(testEmail)
                .role(Role.USER)
                .build())
        );
        when(jwtService.generateToken(any(),any(),any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any(),any(),any())).thenReturn("refresh");

        responseDto = authService.signIn(signInDto);

        assertEquals("access", responseDto.accessToken());
        assertEquals("refresh", responseDto.refreshToken());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound(){
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () -> authService.signIn(signInDto));
        assertEquals(AuthError.USER_NOT_FOUND, exception.getAuthError());
    }

    @Test
    void shouldRefreshTokenWhenValid() {
        Long userId = 1L;
        String oldToken = "oldToken";
        String newToken = "newToken";

        RefreshTokenDto dto = new RefreshTokenDto(userId, oldToken);

        User user = User.builder()
                .id(userId)
                .email(testEmail)
                .role(Role.USER)
                .build();

        when(redisTokenService.getRefreshToken(userId.toString())).thenReturn(oldToken);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtService.generateRefreshToken(any(),any(),any())).thenReturn(newToken);
        when(jwtService.extractExpiration(newToken)).thenReturn(3600000L);

        String result = authService.refreshToken(dto);

        assertEquals(newToken, result);
        verify(jwtService, times(1)).generateRefreshToken(any(),any(),any());
        verify(redisTokenService,times(1)).saveRefreshToken(eq(userId.toString()), eq(newToken), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenInvalid() {
        String providedToken = "providedToken";
        String requiredToken = "requiredToken";
        Long id = 1L;
        RefreshTokenDto dto = new RefreshTokenDto(id, eq(providedToken));

        when(redisTokenService.getRefreshToken(id.toString())).thenReturn(requiredToken);

        AuthException exception = assertThrows(AuthException.class, () -> authService.refreshToken(dto));
        assertEquals(AuthError.INVALID_REFRESH_TOKEN, exception.getAuthError());
    }

    @Test
    void shouldThrowExceptionWhenTokenExpired() {
        RefreshTokenDto dto = new RefreshTokenDto(1L, eq("token"));

        when(redisTokenService.getRefreshToken(Long.valueOf(1L).toString())).thenReturn(null);

        AuthException exception = assertThrows(AuthException.class, () -> authService.refreshToken(dto));
        assertEquals(AuthError.INVALID_REFRESH_TOKEN, exception.getAuthError());
    }
}
