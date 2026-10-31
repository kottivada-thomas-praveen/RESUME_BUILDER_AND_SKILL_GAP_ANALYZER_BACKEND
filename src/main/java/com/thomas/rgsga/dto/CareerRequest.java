package com.thomas.rgsga.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerRequest {

    private String skills;
    private String currentTitle;
    private String experienceLevel;
}
