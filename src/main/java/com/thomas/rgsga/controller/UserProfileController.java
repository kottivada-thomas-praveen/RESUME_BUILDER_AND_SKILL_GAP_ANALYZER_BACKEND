package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.dto.UserProfileDTO;
import com.thomas.rgsga.entity.*;
import com.thomas.rgsga.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "User Profile & Portfolio", description = "Endpoints for user profile, links, skills, experiences, and education")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    @Operation(summary = "Get user portfolio profile")
    public ResponseEntity<ApiResponse<UserProfile>> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("User profile fetched", userProfileService.getProfile(user)));
    }

    @PutMapping
    @Operation(summary = "Update user portfolio profile")
    public ResponseEntity<ApiResponse<UserProfile>> updateProfile(@AuthenticationPrincipal User user,
                                                                   @Valid @RequestBody UserProfileDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("User profile updated", userProfileService.updateProfile(user, dto)));
    }

    // Skills
    @GetMapping("/skills")
    @Operation(summary = "Get all skills for user")
    public ResponseEntity<ApiResponse<List<Skill>>> getSkills(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("User skills fetched", userProfileService.getSkills(user)));
    }

    @PostMapping("/skills")
    @Operation(summary = "Add a new skill")
    public ResponseEntity<ApiResponse<Skill>> addSkill(@AuthenticationPrincipal User user,
                                                       @RequestParam String skillName,
                                                       @RequestParam(required = false) String proficiency) {
        return ResponseEntity.ok(ApiResponse.success("Skill added", userProfileService.addSkill(user, skillName, proficiency)));
    }

    @DeleteMapping("/skills/{id}")
    @Operation(summary = "Delete skill")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(@AuthenticationPrincipal User user, @PathVariable Long id) {
        userProfileService.deleteSkill(user, id);
        return ResponseEntity.ok(ApiResponse.success("Skill deleted", null));
    }

    // Experience
    @GetMapping("/experience")
    @Operation(summary = "Get all work experiences")
    public ResponseEntity<ApiResponse<List<Experience>>> getExperiences(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("User experiences fetched", userProfileService.getExperiences(user)));
    }

    @PostMapping("/experience")
    @Operation(summary = "Add work experience")
    public ResponseEntity<ApiResponse<Experience>> addExperience(@AuthenticationPrincipal User user,
                                                                 @Valid @RequestBody Experience experience) {
        return ResponseEntity.ok(ApiResponse.success("Experience added", userProfileService.addExperience(user, experience)));
    }

    @PutMapping("/experience/{id}")
    @Operation(summary = "Update work experience")
    public ResponseEntity<ApiResponse<Experience>> updateExperience(@AuthenticationPrincipal User user,
                                                                    @PathVariable Long id,
                                                                    @Valid @RequestBody Experience details) {
        return ResponseEntity.ok(ApiResponse.success("Experience updated", userProfileService.updateExperience(user, id, details)));
    }

    @DeleteMapping("/experience/{id}")
    @Operation(summary = "Delete work experience")
    public ResponseEntity<ApiResponse<Void>> deleteExperience(@AuthenticationPrincipal User user, @PathVariable Long id) {
        userProfileService.deleteExperience(user, id);
        return ResponseEntity.ok(ApiResponse.success("Experience deleted", null));
    }

    // Education
    @GetMapping("/education")
    @Operation(summary = "Get all education records")
    public ResponseEntity<ApiResponse<List<Education>>> getEducations(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("User educations fetched", userProfileService.getEducations(user)));
    }

    @PostMapping("/education")
    @Operation(summary = "Add education record")
    public ResponseEntity<ApiResponse<Education>> addEducation(@AuthenticationPrincipal User user,
                                                               @Valid @RequestBody Education education) {
        return ResponseEntity.ok(ApiResponse.success("Education added", userProfileService.addEducation(user, education)));
    }

    @PutMapping("/education/{id}")
    @Operation(summary = "Update education record")
    public ResponseEntity<ApiResponse<Education>> updateEducation(@AuthenticationPrincipal User user,
                                                                  @PathVariable Long id,
                                                                  @Valid @RequestBody Education details) {
        return ResponseEntity.ok(ApiResponse.success("Education updated", userProfileService.updateEducation(user, id, details)));
    }

    @DeleteMapping("/education/{id}")
    @Operation(summary = "Delete education record")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(@AuthenticationPrincipal User user, @PathVariable Long id) {
        userProfileService.deleteEducation(user, id);
        return ResponseEntity.ok(ApiResponse.success("Education deleted", null));
    }
}
