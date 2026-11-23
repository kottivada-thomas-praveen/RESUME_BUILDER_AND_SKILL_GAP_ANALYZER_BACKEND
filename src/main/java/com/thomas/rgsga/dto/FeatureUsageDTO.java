package com.thomas.rgsga.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureUsageDTO {
    private String featureName;
    private String activityType;
    private Long count;
    private Double percentage;
}
