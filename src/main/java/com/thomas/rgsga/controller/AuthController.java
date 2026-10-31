package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.*;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration, login, token refresh, and OAuth endpoints")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Create a new user account")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", authService.signup(request)));
    }

    @PostMapping("/register")
    @Operation(summary = "Alias for user account registration")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", authService.register(request)));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and issue JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.login(request)));
    }

    @GetMapping("/me")
    @Operation(summary = "Retrieve currently authenticated user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("User profile fetched", authService.getCurrentUser(user.getEmail())));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update authenticated user basic information")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@AuthenticationPrincipal User user,
                                                                   @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", authService.updateProfile(user.getEmail(), request)));
    }

    @PostMapping("/logout")
    @Operation(summary = "Log out user and invalidate refresh token")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal User user) {
        if (user != null) {
            authService.logout(user.getEmail());
        }
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @PostMapping({"/refresh", "/refresh-token"})
    @Operation(summary = "Renew access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", authService.refreshToken(request)));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset token")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset instructions dispatched", null));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using reset token / OTP")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password has been reset successfully", null));
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify account email token")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully", null));
    }

    @PostMapping("/google-login")
    @Operation(summary = "Authenticate with Google credentials")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Google login successful", authService.googleLogin(request)));
    }

    @PostMapping("/github-login")
    @Operation(summary = "Authenticate with GitHub authorization code")
    public ResponseEntity<ApiResponse<AuthResponse>> githubLogin(@Valid @RequestBody GithubLoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("GitHub login successful", authService.githubLogin(request)));
    }
}
