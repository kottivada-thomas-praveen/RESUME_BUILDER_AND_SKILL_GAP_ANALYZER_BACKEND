package com.thomas.RG_SGA_.service;

import com.thomas.RG_SGA_.dto.EducationDTO;
import com.thomas.RG_SGA_.dto.ExperienceDTO;
import com.thomas.RG_SGA_.dto.ProjectDTO;
import com.thomas.RG_SGA_.dto.ResumeDTO;
import com.thomas.RG_SGA_.entity.*;
import com.thomas.RG_SGA_.repository.ResumeRepository;
import com.thomas.RG_SGA_.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResumeService {

    private final ResumeRepository resumeRepository;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public List<Resume> getResumes(User user) {
        return resumeRepository.findByUserIdAndIsActiveTrue(user.getId());
    }

    public Resume getResumeById(User user, Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + resumeId));
        if (!resume.getUser().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("Unauthorized to access this resume");
        }
        return resume;
    }

    public Resume createResume(User user, ResumeDTO dto) {
        Resume resume = Resume.builder()
                .user(user)
                .fullName(dto.getFullName() != null ? dto.getFullName() : "Untitled Resume")
                .title(dto.getTitle())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .location(dto.getLocation())
                .website(dto.getWebsite())
                .summary(dto.getSummary())
                .skillsCsv(dto.getSkills())
                .templateName(dto.getTemplateName() != null ? dto.getTemplateName() : "modern")
                .accentColor(dto.getAccentColor() != null ? dto.getAccentColor() : "purple")
                .atsScore(dto.getAtsScore() != null ? dto.getAtsScore() : 0)
                .version(1)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Map experiences
        if (dto.getExperiences() != null) {
            List<Experience> experiences = dto.getExperiences().stream()
                    .map(expDto -> Experience.builder()
                            .user(user)
                            .resume(resume)
                            .company(expDto.getCompany())
                            .role(expDto.getRole())
                            .startDate(expDto.getStartDate())
                            .endDate(expDto.getEndDate())
                            .location(expDto.getLocation())
                            .description(expDto.getDescription())
                            .createdAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());
            resume.setExperiences(experiences);
        }

        // Map educations
        if (dto.getEducations() != null) {
            List<Education> educations = dto.getEducations().stream()
                    .map(eduDto -> Education.builder()
                            .user(user)
                            .resume(resume)
                            .school(eduDto.getSchool())
                            .degree(eduDto.getDegree())
                            .field(eduDto.getField())
                            .gradDate(eduDto.getGradDate())
                            .location(eduDto.getLocation())
                            .createdAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());
            resume.setEducations(educations);
        }

        // Map projects
        if (dto.getProjects() != null) {
            List<Project> projects = dto.getProjects().stream()
                    .map(projDto -> Project.builder()
                            .user(user)
                            .resume(resume)
                            .name(projDto.getName())
                            .technologies(projDto.getTechnologies())
                            .link(projDto.getLink())
                            .description(projDto.getDescription())
                            .createdAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());
            resume.setProjects(projects);
        }

        return resumeRepository.save(resume);
    }

    public Resume updateResume(User user, Long resumeId, ResumeDTO dto) {
        Resume resume = getResumeById(user, resumeId);
        
        resume.setFullName(dto.getFullName() != null ? dto.getFullName() : resume.getFullName());
        resume.setTitle(dto.getTitle());
        resume.setEmail(dto.getEmail());
        resume.setPhone(dto.getPhone());
        resume.setLocation(dto.getLocation());
        resume.setWebsite(dto.getWebsite());
        resume.setSummary(dto.getSummary());
        resume.setSkillsCsv(dto.getSkills());
        resume.setTemplateName(dto.getTemplateName() != null ? dto.getTemplateName() : resume.getTemplateName());
        resume.setAccentColor(dto.getAccentColor() != null ? dto.getAccentColor() : resume.getAccentColor());
        resume.setAtsScore(dto.getAtsScore() != null ? dto.getAtsScore() : resume.getAtsScore());
        resume.setVersion(resume.getVersion() + 1); // bump version on update
        resume.setUpdatedAt(LocalDateTime.now());

        // Clear and replace experiences
        resume.getExperiences().clear();
        if (dto.getExperiences() != null) {
            resume.getExperiences().addAll(dto.getExperiences().stream()
                    .map(expDto -> Experience.builder()
                            .user(user)
                            .resume(resume)
                            .company(expDto.getCompany())
                            .role(expDto.getRole())
                            .startDate(expDto.getStartDate())
                            .endDate(expDto.getEndDate())
                            .location(expDto.getLocation())
                            .description(expDto.getDescription())
                            .createdAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList()));
        }

        // Clear and replace educations
        resume.getEducations().clear();
        if (dto.getEducations() != null) {
            resume.getEducations().addAll(dto.getEducations().stream()
                    .map(eduDto -> Education.builder()
                            .user(user)
                            .resume(resume)
                            .school(eduDto.getSchool())
                            .degree(eduDto.getDegree())
                            .field(eduDto.getField())
                            .gradDate(eduDto.getGradDate())
                            .location(eduDto.getLocation())
                            .createdAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList()));
        }

        // Clear and replace projects
        resume.getProjects().clear();
        if (dto.getProjects() != null) {
            resume.getProjects().addAll(dto.getProjects().stream()
                    .map(projDto -> Project.builder()
                            .user(user)
                            .resume(resume)
                            .name(projDto.getName())
                            .technologies(projDto.getTechnologies())
                            .link(projDto.getLink())
                            .description(projDto.getDescription())
                            .createdAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList()));
        }

        return resumeRepository.save(resume);
    }

    public Resume autosaveResume(User user, Long resumeId, ResumeDTO dto) {
        // Simple light wrapper for update to serve autosaving
        return updateResume(user, resumeId, dto);
    }

    public void deleteResume(User user, Long resumeId) {
        Resume resume = getResumeById(user, resumeId);
        // Soft delete for safety
        resume.setIsActive(false);
        resume.setUpdatedAt(LocalDateTime.now());
        resumeRepository.save(resume);
    }

    /**
     * Map a Resume entity back to its Transfer Object DTO representation.
     */
    public ResumeDTO mapToDTO(Resume resume) {
        List<ExperienceDTO> expDtos = resume.getExperiences().stream()
                .map(exp -> ExperienceDTO.builder()
                        .id(exp.getId())
                        .company(exp.getCompany())
                        .role(exp.getRole())
                        .startDate(exp.getStartDate())
                        .endDate(exp.getEndDate())
                        .location(exp.getLocation())
                        .description(exp.getDescription())
                        .build())
                .collect(Collectors.toList());

        List<EducationDTO> eduDtos = resume.getEducations().stream()
                .map(edu -> EducationDTO.builder()
                        .id(edu.getId())
                        .school(edu.getSchool())
                        .degree(edu.getDegree())
                        .field(edu.getField())
                        .gradDate(edu.getGradDate())
                        .location(edu.getLocation())
                        .build())
                .collect(Collectors.toList());

        List<ProjectDTO> projDtos = resume.getProjects().stream()
                .map(proj -> ProjectDTO.builder()
                        .id(proj.getId())
                        .name(proj.getName())
                        .technologies(proj.getTechnologies())
                        .link(proj.getLink())
                        .description(proj.getDescription())
                        .build())
                .collect(Collectors.toList());

        return ResumeDTO.builder()
                .id(resume.getId())
                .fullName(resume.getFullName())
                .title(resume.getTitle())
                .email(resume.getEmail())
                .phone(resume.getPhone())
                .location(resume.getLocation())
                .website(resume.getWebsite())
                .summary(resume.getSummary())
                .skills(resume.getSkillsCsv())
                .templateName(resume.getTemplateName())
                .accentColor(resume.getAccentColor())
                .atsScore(resume.getAtsScore())
                .version(resume.getVersion())
                .experiences(expDtos)
                .educations(eduDtos)
                .projects(projDtos)
                .build();
    }
}
