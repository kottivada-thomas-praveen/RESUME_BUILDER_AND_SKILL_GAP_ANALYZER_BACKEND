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
public class InterviewRequest {

    @NotBlank(message = "Job title is required")
    @JsonProperty("jobTitle")
    @JsonAlias({"title", "role", "targetRole", "position", "job_title"})
    private String jobTitle;

    @JsonProperty("jobDescription")
    @JsonAlias({"jobDesc", "targetJobDescription", "job", "description", "job_description", "jobText"})
    private String jobDescription;

    @JsonProperty("userSkills")
    @JsonAlias({"skills", "user_skills", "resumeSkills"})
    private String userSkills;

    public void setTitle(String title) {
        if (title != null && !title.isBlank()) {
            this.jobTitle = title;
        }
    }

    public void setRole(String role) {
        if (role != null && !role.isBlank()) {
            this.jobTitle = role;
        }
    }

    public void setTargetRole(String targetRole) {
        if (targetRole != null && !targetRole.isBlank()) {
            this.jobTitle = targetRole;
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

    public void setSkills(String skills) {
        if (skills != null && !skills.isBlank()) {
            this.userSkills = skills;
        }
    }
}
