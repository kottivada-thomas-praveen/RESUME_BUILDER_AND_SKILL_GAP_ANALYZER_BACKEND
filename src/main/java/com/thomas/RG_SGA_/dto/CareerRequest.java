package com.thomas.RG_SGA_.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerRequest {
    private String skills;
    private String currentTitle;
    private String experienceLevel; // "Junior" | "Mid" | "Senior"
}
