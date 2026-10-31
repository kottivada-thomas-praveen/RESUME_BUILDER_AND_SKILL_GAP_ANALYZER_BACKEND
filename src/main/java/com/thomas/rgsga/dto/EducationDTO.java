package com.thomas.rgsga.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationDTO {

    private Long id;
    private String school;
    private String degree;
    private String field;
    private String gradDate;
    private String location;
}
