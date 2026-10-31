package com.thomas.rgsga.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EducationDTO {

    private Long id;

    @JsonProperty("school")
    @JsonAlias({"institution", "university", "college"})
    private String school;

    private String degree;
    private String field;

    @JsonProperty("gradDate")
    @JsonAlias({"grad_date", "graduationDate", "graduation_date", "endDate", "end_date", "year"})
    private String gradDate;

    private String location;

    public void setInstitution(String institution) {
        if (institution != null && !institution.isBlank()) {
            this.school = institution;
        }
    }

    public void setUniversity(String university) {
        if (university != null && !university.isBlank()) {
            this.school = university;
        }
    }

    public void setGraduationDate(String graduationDate) {
        if (graduationDate != null && !graduationDate.isBlank()) {
            this.gradDate = graduationDate;
        }
    }

    public void setEndDate(String endDate) {
        if (endDate != null && !endDate.isBlank()) {
            this.gradDate = endDate;
        }
    }
}
