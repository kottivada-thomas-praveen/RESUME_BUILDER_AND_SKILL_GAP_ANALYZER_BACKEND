package com.thomas.RG_SGA_.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceDTO {
    private Long id;
    private String company;
    private String role;
    private String startDate;
    private String endDate;
    private String location;
    private String description;
}
