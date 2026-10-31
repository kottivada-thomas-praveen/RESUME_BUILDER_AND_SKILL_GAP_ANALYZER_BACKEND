package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.dto.ResumeDTO;
import com.thomas.rgsga.entity.Resume;
import com.thomas.rgsga.service.FileStorageService;
import com.thomas.rgsga.service.PdfExportService;
import com.thomas.rgsga.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/public/resumes")
@Tag(name = "Public Resume Viewer", description = "Public read-only endpoints for shared resumes with view and download tracking")
public class PublicResumeController {

    private final ResumeService resumeService;
    private final PdfExportService pdfExportService;
    private final FileStorageService fileStorageService;

    public PublicResumeController(ResumeService resumeService,
                                  PdfExportService pdfExportService,
                                  FileStorageService fileStorageService) {
        this.resumeService = resumeService;
        this.pdfExportService = pdfExportService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/{shareToken}")
    @Operation(summary = "Get public read-only resume (increments view count)")
    public ResponseEntity<ApiResponse<ResumeDTO>> getPublicResume(@PathVariable String shareToken) {
        ResumeDTO dto = resumeService.getPublicResume(shareToken);
        return ResponseEntity.ok(ApiResponse.success("Public resume loaded successfully", dto));
    }

    @GetMapping("/{shareToken}/export")
    @Operation(summary = "Download public resume PDF (increments download count)")
    public ResponseEntity<byte[]> exportPublicResumePdf(@PathVariable String shareToken) throws IOException {
        Resume resume = resumeService.getPublicResumeEntity(shareToken);
        byte[] pdf = pdfExportService.exportToPdf(resume);

        try {
            fileStorageService.storeResumePdf(resume.getId(), pdf);
        } catch (Exception ignored) {}

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "resume-" + resume.getFullName().replaceAll("\\s+", "_") + ".pdf");
        headers.setContentLength(pdf.length);

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
