package com.thomas.RG_SGA_.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
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
    
    @Builder.Default
    private String skills = ""; // comma-separated skills
    
    @Builder.Default
    private List<ExperienceDTO> experiences = new ArrayList<>();
    
    @Builder.Default
    private List<EducationDTO> educations = new ArrayList<>();
    
    @Builder.Default
    private List<ProjectDTO> projects = new ArrayList<>();
    
    @Builder.Default
    private String templateName = "modern";
    
    @Builder.Default
    private String accentColor = "purple";
    
    @Builder.Default
    private Integer atsScore = 0;
    
    @Builder.Default
    private Integer version = 1;
}
