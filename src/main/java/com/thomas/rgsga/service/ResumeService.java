package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.ResumeDTO;
import com.thomas.rgsga.entity.Resume;
import com.thomas.rgsga.entity.User;

import java.util.List;

public interface ResumeService {

    List<ResumeDTO> getResumes(User user);

    ResumeDTO getResumeById(User user, Long resumeId);

    Resume getResumeEntityById(User user, Long resumeId);

    ResumeDTO createResume(User user, ResumeDTO dto);

    ResumeDTO updateResume(User user, Long resumeId, ResumeDTO dto);

    ResumeDTO autosaveResume(User user, Long resumeId, ResumeDTO dto);

    void deleteResume(User user, Long resumeId);

    ResumeDTO cloneResume(User user, Long resumeId);

    ResumeDTO generateShareToken(User user, Long resumeId);

    ResumeDTO getPublicResume(String shareToken);

    Resume getPublicResumeEntity(String shareToken);

    com.thomas.rgsga.dto.FormattingCheckResponse validateFormatting(ResumeDTO dto);
}
