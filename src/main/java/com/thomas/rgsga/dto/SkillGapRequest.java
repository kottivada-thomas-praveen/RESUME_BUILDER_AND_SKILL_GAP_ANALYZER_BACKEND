package com.thomas.rgsga.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillGapRequest {

    @JsonProperty("userSkills")
    @JsonAlias({"skills", "resumeSkills", "user_skills"})
    private String userSkills;

    @NotBlank(message = "Job description is required")
    @JsonProperty("jobDescription")
    @JsonAlias({"jobDesc", "targetJobDescription", "job", "description", "job_description", "jobText"})
    private String jobDescription;

    public void setSkills(String skills) {
        if (skills != null && !skills.isBlank()) {
            this.userSkills = skills;
        }
    }

    public void setJobDesc(String jobDesc) {
        if (jobDesc != null && !jobDesc.isBlank()) {
            this.jobDescription = jobDesc;
        }
    }

    public void setTargetJobDescription(String targetJobDescription) {
        if (targetJobDescription != null && !targetJobDescription.isBlank()) {
            this.jobDescription = targetJobDescription;
        }
    }

    public void setJobText(String jobText) {
        if (jobText != null && !jobText.isBlank()) {
            this.jobDescription = jobText;
        }
    }
}
