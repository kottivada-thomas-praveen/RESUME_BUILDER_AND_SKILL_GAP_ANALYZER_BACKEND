package com.thomas.rgsga.mapper;

import com.thomas.rgsga.dto.AuthResponse;
import com.thomas.rgsga.dto.UserResponse;
import com.thomas.rgsga.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getName())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accountEnabled(user.getAccountEnabled())
                .emailVerified(user.getEmailVerified())
                .lastLoginAt(user.getLastLoginAt())
                .loginCount(user.getLoginCount())
                .failedLoginCount(user.getFailedLoginCount())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public AuthResponse toAuthResponse(User user, String token, String refreshToken) {
        if (user == null) return null;
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .id(user.getId())
                .fullName(user.getName())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .refreshToken(refreshToken)
                .build();
    }
}
