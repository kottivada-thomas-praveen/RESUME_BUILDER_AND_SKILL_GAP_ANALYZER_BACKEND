package com.thomas.RG_SGA_.controller;

import com.thomas.RG_SGA_.dto.*;
import com.thomas.RG_SGA_.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseWrapper<AuthResponse>> signup(@Valid @RequestBody SignupRequest request) {
        return new ResponseEntity<>(ApiResponseWrapper.success("User registered successfully", authService.signup(request)), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseWrapper<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.success("Login successful", authService.login(request)));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponseWrapper<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.success("Token refreshed successfully", authService.refreshToken(request)));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponseWrapper<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponseWrapper.success("Password reset instructions sent to your email", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseWrapper<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponseWrapper.success("Password has been reset successfully", null));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponseWrapper<Void>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponseWrapper.success("Email verified successfully", null));
    }

    @PostMapping("/google-login")
    public ResponseEntity<ApiResponseWrapper<AuthResponse>> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.success("OAuth login successful", authService.googleLogin(request)));
    }

    @PostMapping("/github-login")
    public ResponseEntity<ApiResponseWrapper<AuthResponse>> githubLogin(@Valid @RequestBody GithubLoginRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.success("OAuth login successful", authService.githubLogin(request)));
    }
}

