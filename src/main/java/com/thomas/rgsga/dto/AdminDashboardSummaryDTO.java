package com.thomas.rgsga.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardSummaryDTO {

    // Visitors
    private Long totalVisitors;
    private Long uniqueVisitors;
    private Long todayVisitors;
    private Long weekVisitors;
    private Long monthVisitors;

    // Users
    private Long totalUsers;
    private Long activeUsers;
    private Long inactiveUsers;
    private Long newUsersToday;
    private Long newUsersThisWeek;

    // Logins
    private Long totalSuccessfulLogins;
    private Long successfulLoginsToday;
    private Long totalFailedLogins;
    private Long failedLoginsToday;
    private Long suspiciousAccountsCount;

    // Activities
    private Long totalResumesCreated;
    private Long totalAtsAnalyses;
    private Long averageAtsScore;
    private Long totalActivities;

    // System Telemetry
    private String systemHealth;
    private String serverUptime;
    private String databaseStatus;
    private Map<String, Long> templatePopularity;
}
