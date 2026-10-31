package com.thomas.rgsga.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoverLetterRequest {

    @NotBlank(message = "Company name is required")
    @JsonProperty("companyName")
    @JsonAlias({"company", "targetCompany", "company_name"})
    private String companyName;

    @NotBlank(message = "Target role is required")
    @JsonProperty("targetRole")
    @JsonAlias({"role", "jobTitle", "title", "position", "target_role"})
    private String targetRole;

    @JsonProperty("applicantName")
    @JsonAlias({"name", "fullName", "applicant_name"})
    private String applicantName;

    @JsonProperty("skills")
    @JsonAlias({"userSkills", "user_skills"})
    private String skills;

    private String experienceYears;
    private String hiringManager;
    private Long resumeId;

    public void setCompany(String company) {
        if (company != null && !company.isBlank()) {
            this.companyName = company;
        }
    }

    public void setRole(String role) {
        if (role != null && !role.isBlank()) {
            this.targetRole = role;
        }
    }

    public void setJobTitle(String jobTitle) {
        if (jobTitle != null && !jobTitle.isBlank()) {
            this.targetRole = jobTitle;
        }
    }

    public void setTitle(String title) {
        if (title != null && !title.isBlank()) {
            this.targetRole = title;
        }
    }

    public void setName(String name) {
        if (name != null && !name.isBlank()) {
            this.applicantName = name;
        }
    }

    public void setFullName(String fullName) {
        if (fullName != null && !fullName.isBlank()) {
            this.applicantName = fullName;
        }
    }

    public void setUserSkills(String userSkills) {
        if (userSkills != null && !userSkills.isBlank()) {
            this.skills = userSkills;
        }
    }
}
