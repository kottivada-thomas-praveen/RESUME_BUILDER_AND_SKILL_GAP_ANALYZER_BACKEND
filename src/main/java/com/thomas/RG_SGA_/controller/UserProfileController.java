package com.thomas.RG_SGA_.controller;

import com.thomas.RG_SGA_.dto.ApiResponseWrapper;
import com.thomas.RG_SGA_.dto.UserProfileDTO;
import com.thomas.RG_SGA_.entity.*;
import com.thomas.RG_SGA_.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<ApiResponseWrapper<UserProfile>> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponseWrapper.success("User profile fetched successfully", userProfileService.getProfile(user)));
    }

    @PutMapping
    public ResponseEntity<ApiResponseWrapper<UserProfile>> updateProfile(@AuthenticationPrincipal User user, 
                                                                         @Valid @RequestBody UserProfileDTO dto) {
        return ResponseEntity.ok(ApiResponseWrapper.success("User profile updated successfully", userProfileService.updateProfile(user, dto)));
    }

    // 2. Skills endpoints
    @GetMapping("/skills")
    public ResponseEntity<ApiResponseWrapper<List<Skill>>> getSkills(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponseWrapper.success("User skills fetched", userProfileService.getSkills(user)));
    }

    @PostMapping("/skills")
    public ResponseEntity<ApiResponseWrapper<Skill>> addSkill(@AuthenticationPrincipal User user, 
                                                              @RequestParam String skillName, 
                                                              @RequestParam(required = false) String proficiency) {
        return ResponseEntity.ok(ApiResponseWrapper.success("Skill added", userProfileService.addSkill(user, skillName, proficiency)));
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> deleteSkill(@AuthenticationPrincipal User user, @PathVariable Long id) {
        userProfileService.deleteSkill(user, id);
        return ResponseEntity.ok(ApiResponseWrapper.success("Skill deleted successfully", null));
    }

    // 3. Experience endpoints
    @GetMapping("/experience")
    public ResponseEntity<ApiResponseWrapper<List<Experience>>> getExperiences(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponseWrapper.success("User experiences fetched", userProfileService.getExperiences(user)));
    }

    @PostMapping("/experience")
    public ResponseEntity<ApiResponseWrapper<Experience>> addExperience(@AuthenticationPrincipal User user, 
                                                                        @Valid @RequestBody Experience experience) {
        return ResponseEntity.ok(ApiResponseWrapper.success("Experience added", userProfileService.addExperience(user, experience)));
    }

    @PutMapping("/experience/{id}")
    public ResponseEntity<ApiResponseWrapper<Experience>> updateExperience(@AuthenticationPrincipal User user, 
                                                                           @PathVariable Long id, 
                                                                           @Valid @RequestBody Experience details) {
        return ResponseEntity.ok(ApiResponseWrapper.success("Experience updated", userProfileService.updateExperience(user, id, details)));
    }

    @DeleteMapping("/experience/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> deleteExperience(@AuthenticationPrincipal User user, @PathVariable Long id) {
        userProfileService.deleteExperience(user, id);
        return ResponseEntity.ok(ApiResponseWrapper.success("Experience deleted successfully", null));
    }

    // 4. Education endpoints
    @GetMapping("/education")
    public ResponseEntity<ApiResponseWrapper<List<Education>>> getEducations(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponseWrapper.success("User education fetched", userProfileService.getEducations(user)));
    }

    @PostMapping("/education")
    public ResponseEntity<ApiResponseWrapper<Education>> addEducation(@AuthenticationPrincipal User user, 
                                                                      @Valid @RequestBody Education education) {
        return ResponseEntity.ok(ApiResponseWrapper.success("Education added", userProfileService.addEducation(user, education)));
    }

    @PutMapping("/education/{id}")
    public ResponseEntity<ApiResponseWrapper<Education>> updateEducation(@AuthenticationPrincipal User user, 
                                                                         @PathVariable Long id, 
                                                                         @Valid @RequestBody Education details) {
        return ResponseEntity.ok(ApiResponseWrapper.success("Education updated", userProfileService.updateEducation(user, id, details)));
    }

    @DeleteMapping("/education/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> deleteEducation(@AuthenticationPrincipal User user, @PathVariable Long id) {
        userProfileService.deleteEducation(user, id);
        return ResponseEntity.ok(ApiResponseWrapper.success("Education deleted successfully", null));
    }
}
