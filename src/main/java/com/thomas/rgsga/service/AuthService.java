package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.*;

public interface AuthService {

    AuthResponse register(SignupRequest request);

    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    UserResponse getCurrentUser(String email);

    UserResponse updateProfile(String email, UpdateProfileRequest request);

    void logout(String email);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void verifyEmail(String token);

    AuthResponse googleLogin(GoogleLoginRequest request);

    AuthResponse githubLogin(GithubLoginRequest request);
}
