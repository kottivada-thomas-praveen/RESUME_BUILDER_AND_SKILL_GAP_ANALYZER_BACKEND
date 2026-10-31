package com.thomas.rgsga.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDTO {

    private Long id;
    private String fullName;
    private String title;
    private String email;
    private String phone;
    private String location;
    private String website;
    private String summary;

    @JsonAlias({"skillsCsv"})
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

    private String targetJobDescription;

    @Builder.Default
    private List<ExperienceDTO> experiences = new ArrayList<>();

    @Builder.Default
    private List<EducationDTO> educations = new ArrayList<>();

    @Builder.Default
    private List<ProjectDTO> projects = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
