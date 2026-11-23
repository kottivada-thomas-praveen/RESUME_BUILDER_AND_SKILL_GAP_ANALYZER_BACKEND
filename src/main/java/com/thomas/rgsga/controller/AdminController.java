package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.*;
import com.thomas.rgsga.entity.AuthAuditLog;
import com.thomas.rgsga.entity.UserActivityLog;
import com.thomas.rgsga.service.AnalyticsService;
import com.thomas.rgsga.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Platform management, user analytics, and system monitoring")
public class AdminController {

    private final DashboardService dashboardService;
    private final AnalyticsService analyticsService;

    public AdminController(DashboardService dashboardService, AnalyticsService analyticsService) {
        this.dashboardService = dashboardService;
        this.analyticsService = analyticsService;
    }

    // =========================================================================
    // 1. Core Platform & Telemetry Summaries
    // =========================================================================

    @GetMapping("/stats")
    @Operation(summary = "Get platform legacy usage metrics (backwards compatible)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPlatformStats() {
        return ResponseEntity.ok(ApiResponse.success("Platform statistics retrieved successfully", dashboardService.getPlatformStatistics()));
    }

    @GetMapping("/dashboard-summary")
    @Operation(summary = "Get comprehensive real-time admin monitoring & analytics KPIs")
    public ResponseEntity<ApiResponse<AdminDashboardSummaryDTO>> getDashboardSummary() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard summary metrics fetched", analyticsService.getDashboardSummary()));
    }

    // =========================================================================
    // 2. Traffic, Pages, Features & Devices Analytics
    // =========================================================================

    @GetMapping("/analytics/traffic")
    @Operation(summary = "Get visitor traffic time-series analytics (daily, weekly, monthly)")
    public ResponseEntity<ApiResponse<TrafficAnalyticsDTO>> getTrafficAnalytics(
            @RequestParam(defaultValue = "daily") String period) {
        return ResponseEntity.ok(ApiResponse.success("Traffic analytics retrieved", analyticsService.getTrafficAnalytics(period)));
    }

    @GetMapping("/analytics/pages")
    @Operation(summary = "Get most visited pages and views distribution")
    public ResponseEntity<ApiResponse<List<PageAnalyticsDTO>>> getTopPages() {
        return ResponseEntity.ok(ApiResponse.success("Top pages telemetry retrieved", analyticsService.getTopPages()));
    }

    @GetMapping("/analytics/features")
    @Operation(summary = "Get platform feature usage breakdown (resumes, ATS, interview, etc.)")
    public ResponseEntity<ApiResponse<List<FeatureUsageDTO>>> getFeatureUsage() {
        return ResponseEntity.ok(ApiResponse.success("Feature usage statistics retrieved", analyticsService.getFeatureUsage()));
    }

    @GetMapping("/analytics/devices")
    @Operation(summary = "Get visitor device type, browser, OS, and geography breakdown")
    public ResponseEntity<ApiResponse<DeviceAnalyticsDTO>> getDeviceAnalytics() {
        return ResponseEntity.ok(ApiResponse.success("Device analytics retrieved", analyticsService.getDeviceAnalytics()));
    }

    @GetMapping("/analytics/suspicious-logins")
    @Operation(summary = "Get flagged accounts with suspicious repeated login failures")
    public ResponseEntity<ApiResponse<List<SuspiciousLoginDTO>>> getSuspiciousLogins() {
        return ResponseEntity.ok(ApiResponse.success("Suspicious login alerts retrieved", analyticsService.getSuspiciousLogins()));
    }

    // =========================================================================
    // 3. User Management
    // =========================================================================

    @GetMapping("/users")
    @Operation(summary = "List registered accounts with optional search, role filter, status filter, and pagination")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", analyticsService.getUsers(search, role, enabled, pageable)));
    }

    @PutMapping("/users/{id}/status")
    @Operation(summary = "Enable or disable a user account")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        boolean enabled = body.getOrDefault("accountEnabled", true);
        analyticsService.updateUserStatus(id, enabled);
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", null));
    }

    // =========================================================================
    // 4. Audit & Activity Logs
    // =========================================================================

    @GetMapping("/auth-logs")
    @Operation(summary = "Get paginated authentication audit logs (logins, failures, password resets)")
    public ResponseEntity<ApiResponse<Page<AuthAuditLog>>> getAuthLogs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String eventType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success("Auth audit logs retrieved", analyticsService.getAuthLogs(search, status, eventType, pageable)));
    }

    @GetMapping("/activity-logs")
    @Operation(summary = "Get paginated user activity logs (resume created, ATS analyzed, etc.)")
    public ResponseEntity<ApiResponse<Page<UserActivityLog>>> getActivityLogs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String activityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success("User activity logs retrieved", analyticsService.getUserActivityLogs(search, activityType, pageable)));
    }

    // =========================================================================
    // 5. CSV Data Exports
    // =========================================================================

    @GetMapping("/export/users")
    @Operation(summary = "Export all registered users to CSV")
    public ResponseEntity<byte[]> exportUsersCsv() {
        byte[] csv = analyticsService.exportUsersCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rbsga-users-" + System.currentTimeMillis() + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }

    @GetMapping("/export/auth-logs")
    @Operation(summary = "Export authentication audit logs to CSV")
    public ResponseEntity<byte[]> exportAuthLogsCsv() {
        byte[] csv = analyticsService.exportAuthLogsCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rbsga-auth-logs-" + System.currentTimeMillis() + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }

    @GetMapping("/export/activity-logs")
    @Operation(summary = "Export user activity stream to CSV")
    public ResponseEntity<byte[]> exportActivityLogsCsv() {
        byte[] csv = analyticsService.exportActivityLogsCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rbsga-activity-logs-" + System.currentTimeMillis() + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }
}
