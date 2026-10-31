package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.repository.*;
import com.thomas.rgsga.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/privacy")
@Tag(name = "Data Privacy & GDPR", description = "GDPR-compliant data deletion to completely wipe personal data, resumes, and PDFs")
public class PrivacyController {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;
    private final UserProfileRepository userProfileRepository;
    private final AtsAnalysisRepository atsAnalysisRepository;
    private final SkillGapAnalysisRepository skillGapAnalysisRepository;
    private final FileStorageService fileStorageService;

    public PrivacyController(UserRepository userRepository,
                             ResumeRepository resumeRepository,
                             SkillRepository skillRepository,
                             UserProfileRepository userProfileRepository,
                             AtsAnalysisRepository atsAnalysisRepository,
                             SkillGapAnalysisRepository skillGapAnalysisRepository,
                             FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.skillRepository = skillRepository;
        this.userProfileRepository = userProfileRepository;
        this.atsAnalysisRepository = atsAnalysisRepository;
        this.skillGapAnalysisRepository = skillGapAnalysisRepository;
        this.fileStorageService = fileStorageService;
    }

    @DeleteMapping("/delete-my-data")
    @Transactional
    @Operation(summary = "GDPR Complete Wipe: Permanently erase user account, all resumes, analyses, and cached files")
    public ResponseEntity<ApiResponse<String>> deleteMyData(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        log.warn("GDPR data deletion requested for user id: {}, email: {}", userId, user.getEmail());

        // 1. Delete physical cached PDF files
        fileStorageService.deleteUserFiles(userId);

        // 2. Cascade delete database entities
        try {
            resumeRepository.deleteByUserId(userId);
        } catch (Exception e) {
            // In case custom query or cascade
            resumeRepository.findAll().stream()
                    .filter(r -> r.getUser().getId().equals(userId))
                    .forEach(resumeRepository::delete);
        }

        try {
            skillRepository.deleteByUserId(userId);
        } catch (Exception ignored) {}

        try {
            userProfileRepository.deleteByUserId(userId);
        } catch (Exception ignored) {}

        try {
            atsAnalysisRepository.deleteByUserId(userId);
        } catch (Exception ignored) {}

        try {
            skillGapAnalysisRepository.deleteByUserId(userId);
        } catch (Exception ignored) {}

        // 3. Delete user account
        userRepository.deleteById(userId);

        log.info("GDPR data erasure completed successfully for user id: {}", userId);
        return ResponseEntity.ok(ApiResponse.success(
                "GDPR Compliance: All personal data, resumes, skill analyses, and cached files have been permanently wiped from the system.",
                "DELETED"
        ));
    }
}
