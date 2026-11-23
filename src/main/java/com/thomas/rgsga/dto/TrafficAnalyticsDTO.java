package com.thomas.rgsga.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrafficAnalyticsDTO {

    private String period; // daily, weekly, monthly
    private Long totalPageViews;
    private Long totalUniqueVisitors;
    private List<TrafficDataPoint> dataPoints;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrafficDataPoint {
        private String label; // e.g., "2026-11-20" or "Mon"
        private Long pageViews;
        private Long uniqueVisitors;
    }
}
