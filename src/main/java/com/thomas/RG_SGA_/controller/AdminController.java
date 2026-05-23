package com.thomas.RG_SGA_.controller;

import com.thomas.RG_SGA_.dto.ApiResponseWrapper;
import com.thomas.RG_SGA_.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final DashboardService dashboardService;

    public AdminController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponseWrapper<Map<String, Object>>> getPlatformStats() {
        return ResponseEntity.ok(ApiResponseWrapper.success("Platform statistics retrieved successfully", dashboardService.getPlatformStatistics()));
    }
}
