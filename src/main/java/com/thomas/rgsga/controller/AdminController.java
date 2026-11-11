package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.dto.UserResponse;
import com.thomas.rgsga.mapper.UserMapper;
import com.thomas.rgsga.service.DashboardService;
import com.thomas.rgsga.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Platform management and aggregated usage analytics")
public class AdminController {

    private final DashboardService dashboardService;
    private final UserService userService;
    private final UserMapper userMapper;

    public AdminController(DashboardService dashboardService, UserService userService, UserMapper userMapper) {
        this.dashboardService = dashboardService;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get platform aggregate usage metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPlatformStats() {
        return ResponseEntity.ok(ApiResponse.success("Platform statistics retrieved successfully", dashboardService.getPlatformStatistics()));
    }

    @GetMapping("/users")
    @Operation(summary = "List all registered accounts on the platform")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }
}