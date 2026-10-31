package com.thomas.rgsga.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResumeDTO {

    private Long id;

    @JsonProperty("fullName")
    @JsonAlias({"name", "full_name"})
    private String fullName;

    private String title;
    private String email;
    private String phone;
    private String location;
    private String website;
    private String summary;

    @JsonProperty("skills")
    @JsonAlias({"skillsCsv", "skills_csv", "userSkills"})
    private String skills;

    @Builder.Default
    private String templateName = "modern";

    @Builder.Default
    private String accentColor = "purple";

    private String photoUrl;

    @Builder.Default
    private Integer atsScore = 0;

    @Builder.Default
    private Integer version = 1;

    private String shareToken;

    @Builder.Default
    private Boolean isPublic = false;

    @Builder.Default
    private Integer viewsCount = 0;

    @Builder.Default
    private Integer downloadCount = 0;

    @JsonProperty("targetJobDescription")
    @JsonAlias({"jobDescription", "jobDesc", "target_job_description"})
    private String targetJobDescription;

    @Builder.Default
    private List<ExperienceDTO> experiences = new ArrayList<>();

    @Builder.Default
    private List<EducationDTO> educations = new ArrayList<>();

    @Builder.Default
    private List<ProjectDTO> projects = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void setName(String name) {
        if (name != null && !name.isBlank()) {
            this.fullName = name;
        }
    }

    public String getName() {
        return this.fullName;
    }

    public void setSkillsCsv(String skillsCsv) {
        if (skillsCsv != null && !skillsCsv.isBlank()) {
            this.skills = skillsCsv;
        }
    }

    public void setJobDescription(String jobDescription) {
        if (jobDescription != null && !jobDescription.isBlank()) {
            this.targetJobDescription = jobDescription;
        }
    }

    public void setJobDesc(String jobDesc) {
        if (jobDesc != null && !jobDesc.isBlank()) {
            this.targetJobDescription = jobDesc;
        }
    }
}
