package com.thomas.rgsga.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageAnalyticsDTO {
    private String pageUrl;
    private String pageTitle;
    private Long pageViews;
    private Long uniqueVisitors;
    private Double viewPercentage;
}
