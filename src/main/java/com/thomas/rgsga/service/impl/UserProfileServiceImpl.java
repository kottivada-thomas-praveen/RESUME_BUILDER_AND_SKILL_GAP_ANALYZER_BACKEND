package com.thomas.rgsga.service.impl;

import com.thomas.rgsga.dto.UserProfileDTO;
import com.thomas.rgsga.entity.*;
import com.thomas.rgsga.exception.ResourceNotFoundException;
import com.thomas.rgsga.repository.*;
import com.thomas.rgsga.service.UserProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final SkillRepository skillRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository,
                                  SkillRepository skillRepository,
                                  ExperienceRepository experienceRepository,
                                  EducationRepository educationRepository) {
        this.userProfileRepository = userProfileRepository;
        this.skillRepository = skillRepository;
        this.experienceRepository = experienceRepository;
        this.educationRepository = educationRepository;
    }

    @Override
    @Transactional
    public UserProfile getProfile(User user) {
        return userProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> userProfileRepository.save(UserProfile.builder().user(user).build()));
    }

    @Override
    public UserProfile updateProfile(User user, UserProfileDTO dto) {
        UserProfile profile = getProfile(user);
        if (dto.getBio() != null) profile.setBio(dto.getBio());
        if (dto.getGithubUrl() != null) profile.setGithubUrl(dto.getGithubUrl());
        if (dto.getLinkedinUrl() != null) profile.setLinkedinUrl(dto.getLinkedinUrl());
        if (dto.getTwitterUrl() != null) profile.setTwitterUrl(dto.getTwitterUrl());
        profile.setUpdatedAt(LocalDateTime.now());
        return userProfileRepository.save(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Skill> getSkills(User user) {
        return skillRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public Skill addSkill(User user, String skillName, String proficiency) {
        String cleanName = skillName.trim();
        return skillRepository.findByUserIdAndSkillNameIgnoreCase(user.getId(), cleanName)
                .map(existing -> {
                    if (proficiency != null) existing.setProficiency(proficiency);
                    return skillRepository.save(existing);
                })
                .orElseGet(() -> skillRepository.save(
                        Skill.builder()
                                .user(user)
                                .skillName(cleanName)
                                .proficiency(proficiency != null ? proficiency : "Intermediate")
                                .build()
                ));
    }

    @Override
    public void deleteSkill(User user, Long skillId) {
        skillRepository.findByIdAndUserId(skillId, user.getId())
                .ifPresent(skillRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Experience> getExperiences(User user) {
        return experienceRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public Experience addExperience(User user, Experience experience) {
        experience.setUser(user);
        return experienceRepository.save(experience);
    }

    @Override
    public Experience updateExperience(User user, Long id, Experience details) {
        Experience existing = experienceRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found: " + id));

        existing.setCompany(details.getCompany());
        existing.setRole(details.getRole());
        existing.setStartDate(details.getStartDate());
        existing.setEndDate(details.getEndDate());
        existing.setLocation(details.getLocation());
        existing.setDescription(details.getDescription());

        return experienceRepository.save(existing);
    }

    @Override
    public void deleteExperience(User user, Long id) {
        experienceRepository.findByIdAndUserId(id, user.getId())
                .ifPresent(experienceRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Education> getEducations(User user) {
        return educationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public Education addEducation(User user, Education education) {
        education.setUser(user);
        return educationRepository.save(education);
    }

    @Override
    public Education updateEducation(User user, Long id, Education details) {
        Education existing = educationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Education not found: " + id));

        existing.setSchool(details.getSchool());
        existing.setDegree(details.getDegree());
        existing.setField(details.getField());
        existing.setGradDate(details.getGradDate());
        existing.setLocation(details.getLocation());

        return educationRepository.save(existing);
    }

    @Override
    public void deleteEducation(User user, Long id) {
        educationRepository.findByIdAndUserId(id, user.getId())
                .ifPresent(educationRepository::delete);
    }
}
