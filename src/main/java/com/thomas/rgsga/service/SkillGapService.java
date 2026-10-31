package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.SkillGapRequest;
import com.thomas.rgsga.dto.SkillGapResponse;
import com.thomas.rgsga.entity.SkillGapAnalysis;
import com.thomas.rgsga.entity.User;

import java.util.List;

public interface SkillGapService {

    SkillGapResponse analyzeSkillGap(User user, SkillGapRequest request);

    List<SkillGapAnalysis> getUserAnalyses(User user);

    SkillGapAnalysis getAnalysisById(User user, Long id);

    void deleteAnalysis(User user, Long id);
}
