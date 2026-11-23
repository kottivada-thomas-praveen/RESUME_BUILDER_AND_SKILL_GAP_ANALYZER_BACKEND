package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.dto.ResumeDTO;
import com.thomas.rgsga.entity.Resume;
import com.thomas.rgsga.entity.User;
import com.thomas.rgsga.service.PdfExportService;
import com.thomas.rgsga.service.ResumeParseService;
import com.thomas.rgsga.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@Tag(name = "Resumes", description = "Resume creation, updates, PDF generation, and resume file parsing")
public class ResumeController {

    private final ResumeService resumeService;
    private final PdfExportService pdfExportService;
    private final ResumeParseService resumeParseService;
    private final com.thomas.rgsga.service.FileStorageService fileStorageService;
    private final com.thomas.rgsga.service.AnalyticsService analyticsService;

    public ResumeController(ResumeService resumeService,
                            PdfExportService pdfExportService,
                            ResumeParseService resumeParseService,
                            com.thomas.rgsga.service.FileStorageService fileStorageService,
                            com.thomas.rgsga.service.AnalyticsService analyticsService) {
        this.resumeService = resumeService;
        this.pdfExportService = pdfExportService;
        this.resumeParseService = resumeParseService;
        this.fileStorageService = fileStorageService;
        this.analyticsService = analyticsService;
    }

    @GetMapping
    @Operation(summary = "Get all active resumes for user")
    public ResponseEntity<ApiResponse<List<ResumeDTO>>> getResumes(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Resumes list fetched", resumeService.getResumes(user)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specific resume by ID")
    public ResponseEntity<ApiResponse<ResumeDTO>> getResume(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Resume fetched successfully", resumeService.getResumeById(user, id)));
    }

    @PostMapping
    @Operation(summary = "Create a new resume")
    public ResponseEntity<ApiResponse<ResumeDTO>> createResume(@AuthenticationPrincipal User user,
                                                               @Valid @RequestBody ResumeDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Resume created successfully", resumeService.createResume(user, dto)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing resume")
    public ResponseEntity<ApiResponse<ResumeDTO>> updateResume(@AuthenticationPrincipal User user,
                                                               @PathVariable Long id,
                                                               @Valid @RequestBody ResumeDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Resume updated successfully", resumeService.updateResume(user, id, dto)));
    }

    @PostMapping("/{id}/clone")
    @Operation(summary = "Clone/duplicate an existing resume")
    public ResponseEntity<ApiResponse<ResumeDTO>> cloneResume(@AuthenticationPrincipal User user, @PathVariable Long id) {
        ResumeDTO cloned = resumeService.cloneResume(user, id);
        return ResponseEntity.ok(ApiResponse.success("Resume cloned successfully", cloned));
    }

    @PostMapping("/{id}/share")
    @Operation(summary = "Generate or retrieve shareable public URL for resume")
    public ResponseEntity<ApiResponse<ResumeDTO>> shareResume(@AuthenticationPrincipal User user, @PathVariable Long id) {
        ResumeDTO shared = resumeService.generateShareToken(user, id);
        return ResponseEntity.ok(ApiResponse.success("Share link created", shared));
    }

    @PostMapping("/validate-format")
    @Operation(summary = "Validate resume word count, page length, and formatting rules")
    public ResponseEntity<ApiResponse<com.thomas.rgsga.dto.FormattingCheckResponse>> validateFormat(@RequestBody ResumeDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Formatting validated", resumeService.validateFormatting(dto)));
    }

    @PostMapping("/{id}/autosave")
    @Operation(summary = "Autosave resume changes in draft state")
    public ResponseEntity<ApiResponse<ResumeDTO>> autosaveResume(@AuthenticationPrincipal User user,
                                                                 @PathVariable Long id,
                                                                 @Valid @RequestBody ResumeDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Resume autosaved", resumeService.autosaveResume(user, id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete resume")
    public ResponseEntity<ApiResponse<Void>> deleteResume(@AuthenticationPrincipal User user, @PathVariable Long id) {
        resumeService.deleteResume(user, id);
        return ResponseEntity.ok(ApiResponse.success("Resume deleted successfully", null));
    }

    @GetMapping("/{id}/export")
    @Operation(summary = "Export resume to formatted PDF document")
    public ResponseEntity<byte[]> exportPdf(@AuthenticationPrincipal User user, @PathVariable Long id) throws IOException {
        Resume resume = resumeService.getResumeEntityById(user, id);
        byte[] pdf = pdfExportService.exportToPdf(resume);

        // Store temporarily in local file storage (SRS #24)
        try {
            fileStorageService.storeResumePdf(id, pdf);
        } catch (Exception e) {
            // Non-fatal caching failure
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "resume-" + id + ".pdf");
        headers.setContentLength(pdf.length);

        analyticsService.logUserActivity(user, "RESUME_DOWNLOADED", "RESUME", id, "Exported PDF for resume: " + resume.getTitle(), null);

        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @PostMapping(value = "/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Parse uploaded PDF/TXT resume into structured JSON")
    public ResponseEntity<ApiResponse<ResumeDTO>> parseResume(@RequestParam("file") MultipartFile file) throws IOException {
        ResumeDTO parsed = resumeParseService.parseResumeFile(file);
        analyticsService.logUserActivity(null, "RESUME_UPLOADED", "RESUME_FILE", null, "Parsed uploaded resume file: " + file.getOriginalFilename(), null);
        return ResponseEntity.ok(ApiResponse.success("Resume parsed successfully", parsed));
    }
}
