package com.thomas.RG_SGA_.service;

import com.thomas.RG_SGA_.dto.UserProfileDTO;
import com.thomas.RG_SGA_.entity.*;
import com.thomas.RG_SGA_.repository.*;
import com.thomas.RG_SGA_.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final SkillRepository skillRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final ProjectRepository projectRepository;

    public UserProfileService(UserProfileRepository userProfileRepository, SkillRepository skillRepository,
                              ExperienceRepository experienceRepository, EducationRepository educationRepository,
                              ProjectRepository projectRepository) {
        this.userProfileRepository = userProfileRepository;
        this.skillRepository = skillRepository;
        this.experienceRepository = experienceRepository;
        this.educationRepository = educationRepository;
        this.projectRepository = projectRepository;
    }

    // 1. User Profile Bio / Links
    public UserProfile getProfile(User user) {
        return userProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserProfile profile = UserProfile.builder()
                            .user(user)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return userProfileRepository.save(profile);
                });
    }

    public UserProfile updateProfile(User user, UserProfileDTO dto) {
        UserProfile profile = getProfile(user);
        profile.setBio(dto.getBio());
        profile.setGithubUrl(dto.getGithubUrl());
        profile.setLinkedinUrl(dto.getLinkedinUrl());
        profile.setTwitterUrl(dto.getTwitterUrl());
        profile.setUpdatedAt(LocalDateTime.now());
        return userProfileRepository.save(profile);
    }

    // 2. Skill Management
    public List<Skill> getSkills(User user) {
        return skillRepository.findByUserId(user.getId());
    }

    public Skill addSkill(User user, String skillName, String proficiency) {
        return skillRepository.findByUserIdAndSkillName(user.getId(), skillName)
                .orElseGet(() -> skillRepository.save(Skill.builder()
                        .user(user)
                        .skillName(skillName)
                        .proficiency(proficiency != null ? proficiency : "Intermediate")
                        .build()));
    }

    public void deleteSkill(User user, Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId));
        if (!skill.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized to delete this skill");
        }
        skillRepository.delete(skill);
    }

    // 3. Experience Management
    public List<Experience> getExperiences(User user) {
        return experienceRepository.findByUserId(user.getId());
    }

    public Experience addExperience(User user, Experience experience) {
        experience.setUser(user);
        return experienceRepository.save(experience);
    }

    public Experience updateExperience(User user, Long expId, Experience details) {
        Experience exp = experienceRepository.findById(expId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found with ID: " + expId));
        if (!exp.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized to modify this experience");
        }
        exp.setCompany(details.getCompany());
        exp.setRole(details.getRole());
        exp.setStartDate(details.getStartDate());
        exp.setEndDate(details.getEndDate());
        exp.setLocation(details.getLocation());
        exp.setDescription(details.getDescription());
        return experienceRepository.save(exp);
    }

    public void deleteExperience(User user, Long expId) {
        Experience exp = experienceRepository.findById(expId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found with ID: " + expId));
        if (!exp.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized to delete this experience");
        }
        experienceRepository.delete(exp);
    }

    // 4. Education Management
    public List<Education> getEducations(User user) {
        return educationRepository.findByUserId(user.getId());
    }

    public Education addEducation(User user, Education education) {
        education.setUser(user);
        return educationRepository.save(education);
    }

    public Education updateEducation(User user, Long eduId, Education details) {
        Education edu = educationRepository.findById(eduId)
                .orElseThrow(() -> new ResourceNotFoundException("Education not found with ID: " + eduId));
        if (!edu.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized to modify this education");
        }
        edu.setSchool(details.getSchool());
        edu.setDegree(details.getDegree());
        edu.setField(details.getField());
        edu.setGradDate(details.getGradDate());
        edu.setLocation(details.getLocation());
        return educationRepository.save(edu);
    }

    public void deleteEducation(User user, Long eduId) {
        Education edu = educationRepository.findById(eduId)
                .orElseThrow(() -> new ResourceNotFoundException("Education not found with ID: " + eduId));
        if (!edu.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized to delete this education");
        }
        educationRepository.delete(edu);
    }
}
