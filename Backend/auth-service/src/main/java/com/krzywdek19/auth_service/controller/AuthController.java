package com.krzywdek19.auth_service.controller;

import com.krzywdek19.auth_service.model.dto.*;
import com.krzywdek19.auth_service.response.AuthResponseDto;
import com.krzywdek19.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpDto signUpDto) {
        authService.signUp(signUpDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponseDto> signin(@Valid @RequestBody SignInDto signInDto) {
        return ResponseEntity.ok(authService.signIn(signInDto));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenDto refreshTokenDto) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenDto));
    }

    @PostMapping("/activate/send-token")
    public ResponseEntity<?> sendActivationToken(@RequestBody EmailDto email) {
        authService.sendActivationToken(email.email());
        return ResponseEntity.ok("Activation token has been sent");
    }

    @PutMapping("/activate/{token}")
    public ResponseEntity<?> activateToken(@PathVariable String token) {
        authService.active(token);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        authService.resetPassword(resetPasswordDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password/send-token")
    public ResponseEntity<?> sendResetPasswordToken(@RequestBody EmailDto email){
        System.out.println(email);
        authService.sendResetPasswordToken(email.email());
        return ResponseEntity.ok("ResetPasswordToken has been sent");
    }

    @PutMapping("/reset-password/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable("token") String token, @RequestBody String password){
        authService.resetPasswordByToken(token, password);
        return ResponseEntity.ok().build();
    }
}
