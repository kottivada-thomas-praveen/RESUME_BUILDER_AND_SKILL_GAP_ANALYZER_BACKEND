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
public class ExperienceDTO {

    private Long id;

    @JsonProperty("company")
    @JsonAlias({"employer", "organization"})
    private String company;

    @JsonProperty("role")
    @JsonAlias({"title", "jobTitle", "position"})
    private String role;

    @JsonProperty("startDate")
    @JsonAlias({"start_date", "from"})
    private String startDate;

    @JsonProperty("endDate")
    @JsonAlias({"end_date", "to"})
    private String endDate;

    private String location;
    private String description;

    public void setTitle(String title) {
        if (title != null && !title.isBlank()) {
            this.role = title;
        }
    }

    public void setJobTitle(String jobTitle) {
        if (jobTitle != null && !jobTitle.isBlank()) {
            this.role = jobTitle;
        }
    }

    public void setEmployer(String employer) {
        if (employer != null && !employer.isBlank()) {
            this.company = employer;
        }
    }
}
