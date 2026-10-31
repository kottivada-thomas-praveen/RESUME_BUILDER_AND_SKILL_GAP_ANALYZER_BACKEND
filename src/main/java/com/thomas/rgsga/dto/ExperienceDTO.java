package com.thomas.rgsga.dto;

import lombok.*;

@Getter
@Setter
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
