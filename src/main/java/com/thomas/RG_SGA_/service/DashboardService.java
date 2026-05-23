package com.thomas.RG_SGA_.service;

import com.thomas.RG_SGA_.dto.UserDTO;
import com.thomas.RG_SGA_.entity.User;
import com.thomas.RG_SGA_.repository.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final AtsAnalysisRepository atsAnalysisRepository;
    private final SkillGapAnalysisRepository skillGapAnalysisRepository;
    private final GithubAnalysisRepository githubAnalysisRepository;

    public DashboardService(UserRepository userRepository, ResumeRepository resumeRepository,
                            AtsAnalysisRepository atsAnalysisRepository,
                            SkillGapAnalysisRepository skillGapAnalysisRepository,
                            GithubAnalysisRepository githubAnalysisRepository) {
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.atsAnalysisRepository = atsAnalysisRepository;
        this.skillGapAnalysisRepository = skillGapAnalysisRepository;
        this.githubAnalysisRepository = githubAnalysisRepository;
    }

    /**
     * Aggregates database numbers to construct system telemetry analytics for administrators.
     */
    public Map<String, Object> getPlatformStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userRepository.count();
        long totalResumes = resumeRepository.count();
        long totalAtsAnalyses = atsAnalysisRepository.count();
        long totalSkillGaps = skillGapAnalysisRepository.count();
        long totalGithubs = githubAnalysisRepository.count();

        stats.put("totalUsers", totalUsers);
        stats.put("totalResumes", totalResumes);
        stats.put("totalAtsAnalyses", totalAtsAnalyses);
        stats.put("totalSkillGapAnalyses", totalSkillGaps);
        stats.put("totalGithubAnalyses", totalGithubs);

        // Get breakdown of users by role
        Map<String, Long> userRoles = userRepository.findAll().stream()
                .collect(Collectors.groupingBy(user -> user.getRole().name(), Collectors.counting()));
        stats.put("userRolesBreakdown", userRoles);

        // Recent user registrations (limit to 5)
        List<UserDTO> recentUsers = userRepository.findAll().stream()
                .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                .limit(5)
                .map(user -> UserDTO.builder()
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .build())
                .collect(Collectors.toList());
        stats.put("recentRegistrations", recentUsers);

        // AI platform usage distribution
        Map<String, Object> aiUsageBreakdown = new HashMap<>();
        aiUsageBreakdown.put("atsScans", totalAtsAnalyses);
        aiUsageBreakdown.put("skillGapRoadmaps", totalSkillGaps);
        aiUsageBreakdown.put("githubReports", totalGithubs);
        stats.put("aiUsageBreakdown", aiUsageBreakdown);

        return stats;
    }
}
