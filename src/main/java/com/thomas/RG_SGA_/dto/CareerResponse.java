package com.thomas.RG_SGA_.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerResponse {
    
    @Builder.Default
    private List<String> recommendedRoles = new ArrayList<>();
    
    @Builder.Default
    private List<RoleSalary> salaryInsights = new ArrayList<>();
    
    @Builder.Default
    private List<DemandItem> marketDemand = new ArrayList<>();
    
    @Builder.Default
    private List<SkillGapResponse.RoadmapStep> roadmap = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleSalary {
        private String role;
        private String salaryRange;
        private String averageSalary;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DemandItem {
        private String role;
        private String demandLevel; // "High" | "Medium" | "Low"
        private String growthRate;  // e.g., "15% YoY"
    }
}
