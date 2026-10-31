package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.dto.ChangePasswordRequest;
import com.thomas.rgsga.dto.UpdateProfileRequest;
import com.thomas.rgsga.dto.UserResponse;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.service.AuthService;
import com.thomas.rgsga.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User settings and security management")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user info")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("User profile fetched", authService.getCurrentUser(user.getEmail())));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user name or email")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@AuthenticationPrincipal User user,
                                                                   @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", authService.updateProfile(user.getEmail(), request)));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password for authenticated user")
    public ResponseEntity<ApiResponse<Void>> changePassword(@AuthenticationPrincipal User user,
                                                            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(user.getEmail(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
}
