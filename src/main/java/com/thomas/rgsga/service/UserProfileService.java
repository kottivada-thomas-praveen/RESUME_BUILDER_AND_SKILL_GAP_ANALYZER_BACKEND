package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.UserProfileDTO;
import com.thomas.rgsga.entity.*;

import java.util.List;

public interface UserProfileService {

    UserProfile getProfile(User user);

    UserProfile updateProfile(User user, UserProfileDTO dto);

    List<Skill> getSkills(User user);

    Skill addSkill(User user, String skillName, String proficiency);

    void deleteSkill(User user, Long skillId);

    List<Experience> getExperiences(User user);

    Experience addExperience(User user, Experience experience);

    Experience updateExperience(User user, Long id, Experience details);

    void deleteExperience(User user, Long id);

    List<Education> getEducations(User user);

    Education addEducation(User user, Education education);

    Education updateEducation(User user, Long id, Education details);

    void deleteEducation(User user, Long id);
}
