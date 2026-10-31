package com.thomas.rgsga.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerResponse {

    @Builder.Default
    private List<CareerRoleItem> recommendedRoles = new ArrayList<>();

    private String salaryInsights;
    private String marketDemand;

    @Builder.Default
    private List<RoadmapStep> roadmap = new ArrayList<>();
}
