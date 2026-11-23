package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.*;
import com.thomas.rgsga.entity.*;
import com.thomas.rgsga.mapper.UserMapper;
import com.thomas.rgsga.repository.*;
import com.thomas.rgsga.service.impl.AnalyticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private VisitorLogRepository visitorLogRepository;

    @Mock
    private AuthAuditLogRepository authAuditLogRepository;

    @Mock
    private UserActivityLogRepository userActivityLogRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private AtsAnalysisRepository atsAnalysisRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .name("Alex Doe")
                .email("alex@example.com")
                .role(Role.USER)
                .accountEnabled(true)
                .loginCount(3)
                .failedLoginCount(0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("trackVisitor successfully persists visitor log")
    void testTrackVisitor() {
        VisitorTrackRequest request = VisitorTrackRequest.builder()
                .visitorId("vid-12345")
                .sessionId("sid-67890")
                .pageUrl("/dashboard")
                .pageTitle("Dashboard Overview")
                .referrerUrl("/")
                .deviceType("Desktop")
                .browser("Chrome")
                .os("Windows")
                .durationSeconds(45)
                .build();

        when(visitorLogRepository.save(any(VisitorLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VisitorLog saved = analyticsService.trackVisitor(request, null);

        assertThat(saved).isNotNull();
        assertThat(saved.getVisitorId()).isEqualTo("vid-12345");
        assertThat(saved.getPageUrl()).isEqualTo("/dashboard");
        verify(visitorLogRepository, times(1)).save(any(VisitorLog.class));
    }

    @Test
    @DisplayName("logAuthEvent records authentication audit event")
    void testLogAuthEvent() {
        analyticsService.logAuthEvent("alex@example.com", "LOGIN_SUCCESS", "SUCCESS", null, null, sampleUser);
        verify(authAuditLogRepository, times(1)).save(any(AuthAuditLog.class));
    }

    @Test
    @DisplayName("logUserActivity records user activity event")
    void testLogUserActivity() {
        analyticsService.logUserActivity(sampleUser, "RESUME_CREATED", "RESUME", 10L, "Created resume Title", null);
        verify(userActivityLogRepository, times(1)).save(any(UserActivityLog.class));
    }

    @Test
    @DisplayName("getDashboardSummary calculates core metrics accurately")
    void testGetDashboardSummary() {
        when(visitorLogRepository.count()).thenReturn(100L);
        when(visitorLogRepository.countDistinctVisitors()).thenReturn(50L);
        when(visitorLogRepository.countDistinctVisitorsSince(any())).thenReturn(10L);
        when(userRepository.count()).thenReturn(20L);
        when(userRepository.countByAccountEnabledTrue()).thenReturn(18L);
        when(userRepository.countByAccountEnabledFalse()).thenReturn(2L);
        when(userRepository.countByCreatedAtAfter(any())).thenReturn(3L);
        when(authAuditLogRepository.countByEventType("LOGIN_SUCCESS")).thenReturn(40L);
        when(authAuditLogRepository.countByEventType("LOGIN_FAILURE")).thenReturn(5L);
        when(resumeRepository.count()).thenReturn(25L);
        when(atsAnalysisRepository.count()).thenReturn(15L);
        when(atsAnalysisRepository.findAverageScore()).thenReturn(84.5);
        when(userActivityLogRepository.count()).thenReturn(70L);

        AdminDashboardSummaryDTO summary = analyticsService.getDashboardSummary();

        assertThat(summary).isNotNull();
        assertThat(summary.getTotalVisitors()).isEqualTo(100L);
        assertThat(summary.getUniqueVisitors()).isEqualTo(50L);
        assertThat(summary.getTotalUsers()).isEqualTo(20L);
        assertThat(summary.getActiveUsers()).isEqualTo(18L);
        assertThat(summary.getAverageAtsScore()).isEqualTo(85L);
        assertThat(summary.getSystemHealth()).contains("HEALTHY");
    }

    @Test
    @DisplayName("updateUserStatus enables and disables user successfully")
    void testUpdateUserStatus() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        analyticsService.updateUserStatus(1L, false);

        assertThat(sampleUser.getAccountEnabled()).isFalse();
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("exportUsersCsv produces well-formatted CSV bytes")
    void testExportUsersCsv() {
        when(userRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(sampleUser));

        byte[] csv = analyticsService.exportUsersCsv();

        assertThat(csv).isNotEmpty();
        String content = new String(csv);
        assertThat(content).contains("ID,Name,Email,Role,AccountEnabled");
        assertThat(content).contains("alex@example.com");
    }
}
