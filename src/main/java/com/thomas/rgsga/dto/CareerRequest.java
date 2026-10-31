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
public class CareerRequest {

    @JsonProperty("skills")
    @JsonAlias({"userSkills", "user_skills"})
    private String skills;

    @JsonProperty("currentTitle")
    @JsonAlias({"title", "role", "current_title", "targetRole"})
    private String currentTitle;

    @JsonProperty("experienceLevel")
    @JsonAlias({"experience", "level", "experience_level"})
    private String experienceLevel;

    public void setUserSkills(String userSkills) {
        if (userSkills != null && !userSkills.isBlank()) {
            this.skills = userSkills;
        }
    }

    public void setTitle(String title) {
        if (title != null && !title.isBlank()) {
            this.currentTitle = title;
        }
    }

    public void setRole(String role) {
        if (role != null && !role.isBlank()) {
            this.currentTitle = role;
        }
    }

    public void setExperience(String experience) {
        if (experience != null && !experience.isBlank()) {
            this.experienceLevel = experience;
        }
    }
}
