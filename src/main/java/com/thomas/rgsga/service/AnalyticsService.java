package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.*;
import com.thomas.rgsga.entity.AuthAuditLog;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.entity.UserActivityLog;
import com.thomas.rgsga.entity.VisitorLog;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AnalyticsService {

    // 1. Visitor Tracking
    VisitorLog trackVisitor(VisitorTrackRequest request, HttpServletRequest httpRequest);

    // 2. Authentication Audit Logging
    void logAuthEvent(String email, String eventType, String status, String failureReason, HttpServletRequest request, User user);

    // 3. User Activity Tracking
    void logUserActivity(User user, String activityType, String entityType, Long entityId, String details, HttpServletRequest request);

    // 4. Admin Dashboard Metrics & Analytics
    AdminDashboardSummaryDTO getDashboardSummary();

    TrafficAnalyticsDTO getTrafficAnalytics(String period);

    List<PageAnalyticsDTO> getTopPages();

    List<FeatureUsageDTO> getFeatureUsage();

    DeviceAnalyticsDTO getDeviceAnalytics();

    List<SuspiciousLoginDTO> getSuspiciousLogins();

    // 5. Audit Log Queries
    Page<AuthAuditLog> getAuthLogs(String query, String status, String eventType, Pageable pageable);

    Page<UserActivityLog> getUserActivityLogs(String query, String activityType, Pageable pageable);

    Page<UserResponse> getUsers(String query, String role, Boolean enabled, Pageable pageable);

    void updateUserStatus(Long userId, boolean enabled);

    // 6. CSV Export Generators
    byte[] exportUsersCsv();

    byte[] exportAuthLogsCsv();

    byte[] exportActivityLogsCsv();
}
