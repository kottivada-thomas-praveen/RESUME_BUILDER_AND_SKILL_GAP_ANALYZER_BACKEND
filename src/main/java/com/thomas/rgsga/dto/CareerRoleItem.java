package com.thomas.rgsga.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerRoleItem {

    @JsonAlias({"role", "jobTitle"})
    private String role;

    @Builder.Default
    private Integer matchScore = 85;

    @Builder.Default
    private String demandLevel = "High";

    private String avgSalary;

    @Builder.Default
    private List<String> requiredSkills = new ArrayList<>();

    @Builder.Default
    private String transitionDifficulty = "Moderate";
}
