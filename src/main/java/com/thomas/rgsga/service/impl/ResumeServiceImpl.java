package com.thomas.rgsga.service.impl;

import com.thomas.rgsga.dto.ResumeDTO;
import com.thomas.rgsga.entity.Resume;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.exception.ResourceNotFoundException;
import com.thomas.rgsga.mapper.ResumeMapper;
import com.thomas.rgsga.repository.ResumeRepository;
import com.thomas.rgsga.service.AnalyticsService;
import com.thomas.rgsga.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeMapper resumeMapper;
    private final AnalyticsService analyticsService;

    public ResumeServiceImpl(ResumeRepository resumeRepository,
                             ResumeMapper resumeMapper,
                             AnalyticsService analyticsService) {
        this.resumeRepository = resumeRepository;
        this.resumeMapper = resumeMapper;
        this.analyticsService = analyticsService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumeDTO> getResumes(User user) {
        return resumeRepository.findByUserIdAndIsActiveTrueOrderByUpdatedAtDesc(user.getId())
                .stream()
                .map(resumeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeDTO getResumeById(User user, Long resumeId) {
        return resumeMapper.toDTO(getResumeEntityById(user, resumeId));
    }

    @Override
    @Transactional(readOnly = true)
    public Resume getResumeEntityById(User user, Long resumeId) {
        Resume resume = resumeRepository.findByIdAndUserIdWithDetails(resumeId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + resumeId));
        if (resume.getExperiences() != null) resume.getExperiences().size();
        if (resume.getEducations() != null) resume.getEducations().size();
        if (resume.getProjects() != null) resume.getProjects().size();
        return resume;
    }

    @Override
    public ResumeDTO createResume(User user, ResumeDTO dto) {
        Resume resume = resumeMapper.toEntity(dto, user);
        Resume saved = resumeRepository.save(resume);
        analyticsService.logUserActivity(user, "RESUME_CREATED", "RESUME", saved.getId(), "Created resume: " + saved.getTitle(), null);
        log.info("Created new resume id [{}] for user [{}]", saved.getId(), user.getEmail());
        return resumeMapper.toDTO(saved);
    }

    @Override
    public ResumeDTO updateResume(User user, Long resumeId, ResumeDTO dto) {
        Resume resume = getResumeEntityById(user, resumeId);
        resumeMapper.updateEntityFromDTO(resume, dto, user);
        Resume updated = resumeRepository.save(resume);
        analyticsService.logUserActivity(user, "RESUME_UPDATED", "RESUME", updated.getId(), "Updated resume: " + updated.getTitle(), null);
        log.info("Updated resume id [{}] for user [{}]", updated.getId(), user.getEmail());
        return resumeMapper.toDTO(updated);
    }

    @Override
    public ResumeDTO autosaveResume(User user, Long resumeId, ResumeDTO dto) {
        return updateResume(user, resumeId, dto);
    }

    @Override
    public void deleteResume(User user, Long resumeId) {
        Resume resume = getResumeEntityById(user, resumeId);
        resume.setIsActive(false);
        resumeRepository.save(resume);
        analyticsService.logUserActivity(user, "RESUME_DELETED", "RESUME", resumeId, "Soft deleted resume id: " + resumeId, null);
        log.info("Soft deleted resume id [{}] for user [{}]", resumeId, user.getEmail());
    }

    @Override
    public ResumeDTO cloneResume(User user, Long resumeId) {
        Resume source = getResumeEntityById(user, resumeId);
        Resume cloned = Resume.builder()
                .user(user)
                .fullName(source.getFullName())
                .title((source.getTitle() != null ? source.getTitle() : "Software Engineer") + " (Copy)")
                .email(source.getEmail())
                .phone(source.getPhone())
                .location(source.getLocation())
                .website(source.getWebsite())
                .summary(source.getSummary())
                .skillsCsv(source.getSkillsCsv())
                .templateName(source.getTemplateName())
                .accentColor(source.getAccentColor())
                .photoUrl(source.getPhotoUrl())
                .atsScore(source.getAtsScore())
                .version(source.getVersion() != null ? source.getVersion() + 1 : 2)
                .targetJobDescription(source.getTargetJobDescription())
                .isPublic(false)
                .viewsCount(0)
                .downloadCount(0)
                .isActive(true)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        if (source.getExperiences() != null) {
            for (com.thomas.rgsga.entity.Experience exp : source.getExperiences()) {
                cloned.getExperiences().add(com.thomas.rgsga.entity.Experience.builder()
                        .user(user)
                        .resume(cloned)
                        .company(exp.getCompany())
                        .role(exp.getRole())
                        .startDate(exp.getStartDate())
                        .endDate(exp.getEndDate())
                        .location(exp.getLocation())
                        .description(exp.getDescription())
                        .build());
            }
        }

        if (source.getEducations() != null) {
            for (com.thomas.rgsga.entity.Education edu : source.getEducations()) {
                cloned.getEducations().add(com.thomas.rgsga.entity.Education.builder()
                        .user(user)
                        .resume(cloned)
                        .school(edu.getSchool())
                        .degree(edu.getDegree())
                        .field(edu.getField())
                        .gradDate(edu.getGradDate())
                        .location(edu.getLocation())
                        .build());
            }
        }

        if (source.getProjects() != null) {
            for (com.thomas.rgsga.entity.Project proj : source.getProjects()) {
                cloned.getProjects().add(com.thomas.rgsga.entity.Project.builder()
                        .user(user)
                        .resume(cloned)
                        .name(proj.getName())
                        .technologies(proj.getTechnologies())
                        .link(proj.getLink())
                        .description(proj.getDescription())
                        .build());
            }
        }

        Resume saved = resumeRepository.save(cloned);
        log.info("Cloned resume [{}] into new resume [{}] for user [{}]", resumeId, saved.getId(), user.getEmail());
        return resumeMapper.toDTO(saved);
    }

    @Override
    public ResumeDTO generateShareToken(User user, Long resumeId) {
        Resume resume = getResumeEntityById(user, resumeId);
        if (resume.getShareToken() == null || resume.getShareToken().isBlank()) {
            resume.setShareToken(java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        }
        resume.setIsPublic(true);
        Resume updated = resumeRepository.save(resume);
        return resumeMapper.toDTO(updated);
    }

    @Override
    public ResumeDTO getPublicResume(String shareToken) {
        Resume resume = resumeRepository.findByShareTokenWithDetails(shareToken)
                .orElseThrow(() -> new ResourceNotFoundException("Public resume not found with token: " + shareToken));
        resume.setViewsCount((resume.getViewsCount() != null ? resume.getViewsCount() : 0) + 1);
        resumeRepository.save(resume);
        return resumeMapper.toDTO(resume);
    }

    @Override
    @Transactional
    public Resume getPublicResumeEntity(String shareToken) {
        Resume resume = resumeRepository.findByShareTokenWithDetails(shareToken)
                .orElseThrow(() -> new ResourceNotFoundException("Public resume not found with token: " + shareToken));
        resume.setDownloadCount((resume.getDownloadCount() != null ? resume.getDownloadCount() : 0) + 1);
        Resume saved = resumeRepository.save(resume);
        if (saved.getExperiences() != null) saved.getExperiences().size();
        if (saved.getEducations() != null) saved.getEducations().size();
        if (saved.getProjects() != null) saved.getProjects().size();
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public com.thomas.rgsga.dto.FormattingCheckResponse validateFormatting(ResumeDTO dto) {
        StringBuilder fullText = new StringBuilder();
        if (dto.getFullName() != null) fullText.append(dto.getFullName()).append(" ");
        if (dto.getTitle() != null) fullText.append(dto.getTitle()).append(" ");
        if (dto.getSummary() != null) fullText.append(dto.getSummary()).append(" ");
        if (dto.getSkills() != null) fullText.append(dto.getSkills()).append(" ");
        if (dto.getExperiences() != null) {
            dto.getExperiences().forEach(e -> fullText.append(e.getRole()).append(" ")
                    .append(e.getCompany()).append(" ")
                    .append(e.getDescription()).append(" "));
        }
        if (dto.getEducations() != null) {
            dto.getEducations().forEach(ed -> fullText.append(ed.getDegree()).append(" ")
                    .append(ed.getSchool()).append(" "));
        }
        if (dto.getProjects() != null) {
            dto.getProjects().forEach(p -> fullText.append(p.getName()).append(" ")
                    .append(p.getTechnologies()).append(" ")
                    .append(p.getDescription()).append(" "));
        }

        String content = fullText.toString().trim();
        String[] words = content.isEmpty() ? new String[0] : content.split("\\s+");
        int wordCount = words.length;
        int charCount = content.length();

        int estimatedPages = 1;
        if (wordCount > 950) {
            estimatedPages = 3;
        } else if (wordCount > 480) {
            estimatedPages = 2;
        }

        List<String> warnings = new java.util.ArrayList<>();
        List<String> suggestions = new java.util.ArrayList<>();
        int formattingScore = 100;

        if (estimatedPages >= 3) {
            warnings.add("Resume length (" + wordCount + " words) will likely spill onto 3 pages. Recruiter standard recommends keeping resumes to 1-2 pages maximum.");
            formattingScore -= 25;
        } else if (estimatedPages == 2) {
            suggestions.add("Current length is estimated at 2 pages. Ensure experience bullet points remain dense with metrics.");
        } else {
            suggestions.add("Optimal 1-page length for concise ATS scanning.");
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank() || dto.getPhone() == null || dto.getPhone().isBlank()) {
            warnings.add("Missing contact details: Ensure email and phone number are provided for recruiters.");
            formattingScore -= 15;
        }

        if (dto.getSummary() == null || dto.getSummary().trim().length() < 50) {
            suggestions.add("Professional Summary is short or absent. A strong 3-4 sentence elevator pitch increases interview callback rates.");
            formattingScore -= 10;
        }

        if (dto.getExperiences() == null || dto.getExperiences().isEmpty()) {
            warnings.add("No experience items listed. Include work history, internships, or freelance engagements.");
            formattingScore -= 20;
        }

        return com.thomas.rgsga.dto.FormattingCheckResponse.builder()
                .estimatedPages(estimatedPages)
                .wordCount(wordCount)
                .characterCount(charCount)
                .isLengthOptimal(estimatedPages <= 2)
                .warnings(warnings)
                .suggestions(suggestions)
                .formattingScore(Math.max(30, formattingScore))
                .build();
    }
}
