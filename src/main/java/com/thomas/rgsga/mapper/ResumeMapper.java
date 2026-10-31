package com.thomas.rgsga.mapper;

import com.thomas.rgsga.dto.EducationDTO;
import com.thomas.rgsga.dto.ExperienceDTO;
import com.thomas.rgsga.dto.ProjectDTO;
import com.thomas.rgsga.dto.ResumeDTO;
import com.thomas.rgsga.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResumeMapper {

    public ResumeDTO toDTO(Resume entity) {
        if (entity == null) return null;

        List<ExperienceDTO> experiences = entity.getExperiences() != null
                ? entity.getExperiences().stream().map(this::toExperienceDTO).collect(Collectors.toList())
                : new ArrayList<>();

        List<EducationDTO> educations = entity.getEducations() != null
                ? entity.getEducations().stream().map(this::toEducationDTO).collect(Collectors.toList())
                : new ArrayList<>();

        List<ProjectDTO> projects = entity.getProjects() != null
                ? entity.getProjects().stream().map(this::toProjectDTO).collect(Collectors.toList())
                : new ArrayList<>();

        return ResumeDTO.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .title(entity.getTitle())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .location(entity.getLocation())
                .website(entity.getWebsite())
                .summary(entity.getSummary())
                .skills(entity.getSkillsCsv())
                .templateName(entity.getTemplateName())
                .accentColor(entity.getAccentColor())
                .photoUrl(entity.getPhotoUrl())
                .atsScore(entity.getAtsScore())
                .version(entity.getVersion())
                .shareToken(entity.getShareToken())
                .isPublic(entity.getIsPublic())
                .viewsCount(entity.getViewsCount())
                .downloadCount(entity.getDownloadCount())
                .targetJobDescription(entity.getTargetJobDescription())
                .experiences(experiences)
                .educations(educations)
                .projects(projects)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Resume toEntity(ResumeDTO dto, User user) {
        if (dto == null) return null;

        Resume resume = Resume.builder()
                .user(user)
                .fullName(dto.getFullName() != null && !dto.getFullName().isBlank() ? dto.getFullName() : "Untitled Resume")
                .title(dto.getTitle())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .location(dto.getLocation())
                .website(dto.getWebsite())
                .summary(dto.getSummary())
                .skillsCsv(dto.getSkills())
                .templateName(dto.getTemplateName() != null ? dto.getTemplateName() : "modern")
                .accentColor(dto.getAccentColor() != null ? dto.getAccentColor() : "purple")
                .photoUrl(dto.getPhotoUrl())
                .atsScore(dto.getAtsScore() != null ? dto.getAtsScore() : 0)
                .version(dto.getVersion() != null ? dto.getVersion() : 1)
                .shareToken(dto.getShareToken())
                .isPublic(dto.getIsPublic() != null ? dto.getIsPublic() : false)
                .viewsCount(dto.getViewsCount() != null ? dto.getViewsCount() : 0)
                .downloadCount(dto.getDownloadCount() != null ? dto.getDownloadCount() : 0)
                .targetJobDescription(dto.getTargetJobDescription())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        if (dto.getExperiences() != null) {
            List<Experience> experiences = dto.getExperiences().stream()
                    .map(e -> toExperienceEntity(e, user, resume))
                    .collect(Collectors.toList());
            resume.setExperiences(experiences);
        }

        if (dto.getEducations() != null) {
            List<Education> educations = dto.getEducations().stream()
                    .map(e -> toEducationEntity(e, user, resume))
                    .collect(Collectors.toList());
            resume.setEducations(educations);
        }

        if (dto.getProjects() != null) {
            List<Project> projects = dto.getProjects().stream()
                    .map(p -> toProjectEntity(p, user, resume))
                    .collect(Collectors.toList());
            resume.setProjects(projects);
        }

        return resume;
    }

    public void updateEntityFromDTO(Resume entity, ResumeDTO dto, User user) {
        if (dto.getFullName() != null && !dto.getFullName().isBlank()) entity.setFullName(dto.getFullName());
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getPhone() != null) entity.setPhone(dto.getPhone());
        if (dto.getLocation() != null) entity.setLocation(dto.getLocation());
        if (dto.getWebsite() != null) entity.setWebsite(dto.getWebsite());
        if (dto.getSummary() != null) entity.setSummary(dto.getSummary());
        if (dto.getSkills() != null) entity.setSkillsCsv(dto.getSkills());
        if (dto.getTemplateName() != null) entity.setTemplateName(dto.getTemplateName());
        if (dto.getAccentColor() != null) entity.setAccentColor(dto.getAccentColor());
        if (dto.getPhotoUrl() != null) entity.setPhotoUrl(dto.getPhotoUrl());
        if (dto.getAtsScore() != null) entity.setAtsScore(dto.getAtsScore());
        if (dto.getShareToken() != null) entity.setShareToken(dto.getShareToken());
        if (dto.getIsPublic() != null) entity.setIsPublic(dto.getIsPublic());
        if (dto.getTargetJobDescription() != null) entity.setTargetJobDescription(dto.getTargetJobDescription());
        entity.setUpdatedAt(LocalDateTime.now());

        if (dto.getExperiences() != null) {
            entity.getExperiences().clear();
            List<Experience> newExp = dto.getExperiences().stream()
                    .map(e -> toExperienceEntity(e, user, entity))
                    .collect(Collectors.toList());
            entity.getExperiences().addAll(newExp);
        }

        if (dto.getEducations() != null) {
            entity.getEducations().clear();
            List<Education> newEdu = dto.getEducations().stream()
                    .map(e -> toEducationEntity(e, user, entity))
                    .collect(Collectors.toList());
            entity.getEducations().addAll(newEdu);
        }

        if (dto.getProjects() != null) {
            entity.getProjects().clear();
            List<Project> newProj = dto.getProjects().stream()
                    .map(p -> toProjectEntity(p, user, entity))
                    .collect(Collectors.toList());
            entity.getProjects().addAll(newProj);
        }
    }

    public ExperienceDTO toExperienceDTO(Experience entity) {
        if (entity == null) return null;
        return ExperienceDTO.builder()
                .id(entity.getId())
                .company(entity.getCompany())
                .role(entity.getRole())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .location(entity.getLocation())
                .description(entity.getDescription())
                .build();
    }

    public Experience toExperienceEntity(ExperienceDTO dto, User user, Resume resume) {
        if (dto == null) return null;
        return Experience.builder()
                .user(user)
                .resume(resume)
                .company(dto.getCompany() != null ? dto.getCompany() : "")
                .role(dto.getRole() != null ? dto.getRole() : "")
                .startDate(dto.getStartDate() != null ? dto.getStartDate() : "")
                .endDate(dto.getEndDate())
                .location(dto.getLocation())
                .description(dto.getDescription())
                .build();
    }

    public EducationDTO toEducationDTO(Education entity) {
        if (entity == null) return null;
        return EducationDTO.builder()
                .id(entity.getId())
                .school(entity.getSchool())
                .degree(entity.getDegree())
                .field(entity.getField())
                .gradDate(entity.getGradDate())
                .location(entity.getLocation())
                .build();
    }

    public Education toEducationEntity(EducationDTO dto, User user, Resume resume) {
        if (dto == null) return null;
        return Education.builder()
                .user(user)
                .resume(resume)
                .school(dto.getSchool() != null ? dto.getSchool() : "")
                .degree(dto.getDegree() != null ? dto.getDegree() : "")
                .field(dto.getField())
                .gradDate(dto.getGradDate())
                .location(dto.getLocation())
                .build();
    }

    public ProjectDTO toProjectDTO(Project entity) {
        if (entity == null) return null;
        return ProjectDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .technologies(entity.getTechnologies())
                .link(entity.getLink())
                .description(entity.getDescription())
                .build();
    }

    public Project toProjectEntity(ProjectDTO dto, User user, Resume resume) {
        if (dto == null) return null;
        return Project.builder()
                .user(user)
                .resume(resume)
                .name(dto.getName() != null ? dto.getName() : "")
                .technologies(dto.getTechnologies())
                .link(dto.getLink())
                .description(dto.getDescription())
                .build();
    }
}
