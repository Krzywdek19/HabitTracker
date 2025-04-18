package com.krzywdek19.auth_service.controller;

import com.krzywdek19.auth_service.model.dto.RefreshTokenDto;
import com.krzywdek19.auth_service.model.dto.SignInDto;
import com.krzywdek19.auth_service.model.dto.SignUpDto;
import com.krzywdek19.auth_service.response.AuthResponseDto;
import com.krzywdek19.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpDto signUpDto) {
        authService.signUp(signUpDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponseDto> signin(@Valid @RequestBody SignInDto signInDto) {
        return ResponseEntity.ok(authService.signIn(signInDto));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenDto refreshTokenDto) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenDto));
    }
}
