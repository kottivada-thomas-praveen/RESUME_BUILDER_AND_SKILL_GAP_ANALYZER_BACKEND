package com.thomas.RG_SGA_.controller;

import com.thomas.RG_SGA_.dto.ApiResponseWrapper;
import com.thomas.RG_SGA_.dto.ResumeDTO;
import com.thomas.RG_SGA_.entity.Resume;
import com.thomas.RG_SGA_.entity.User;
import com.thomas.RG_SGA_.service.ParserEngineService;
import com.thomas.RG_SGA_.service.PdfExportService;
import com.thomas.RG_SGA_.service.ResumeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final ParserEngineService parserEngineService;
    private final PdfExportService pdfExportService;

    public ResumeController(ResumeService resumeService, ParserEngineService parserEngineService,
                            PdfExportService pdfExportService) {
        this.resumeService = resumeService;
        this.parserEngineService = parserEngineService;
        this.pdfExportService = pdfExportService;
    }

    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<ResumeDTO>>> getResumes(@AuthenticationPrincipal User user) {
        List<ResumeDTO> list = resumeService.getResumes(user).stream()
                .map(resumeService::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponseWrapper.success("Resumes list fetched", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<ResumeDTO>> getResume(@AuthenticationPrincipal User user, @PathVariable Long id) {
        Resume resume = resumeService.getResumeById(user, id);
        return ResponseEntity.ok(ApiResponseWrapper.success("Resume fetched successfully", resumeService.mapToDTO(resume)));
    }

    @PostMapping
    public ResponseEntity<ApiResponseWrapper<ResumeDTO>> createResume(@AuthenticationPrincipal User user, 
                                                                      @Valid @RequestBody ResumeDTO dto) {
        Resume created = resumeService.createResume(user, dto);
        return ResponseEntity.ok(ApiResponseWrapper.success("Resume created successfully", resumeService.mapToDTO(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<ResumeDTO>> updateResume(@AuthenticationPrincipal User user, 
                                                                      @PathVariable Long id, 
                                                                      @Valid @RequestBody ResumeDTO dto) {
        Resume updated = resumeService.updateResume(user, id, dto);
        return ResponseEntity.ok(ApiResponseWrapper.success("Resume updated successfully", resumeService.mapToDTO(updated)));
    }

    @PostMapping("/{id}/autosave")
    public ResponseEntity<ApiResponseWrapper<ResumeDTO>> autosaveResume(@AuthenticationPrincipal User user, 
                                                                        @PathVariable Long id, 
                                                                        @Valid @RequestBody ResumeDTO dto) {
        Resume updated = resumeService.autosaveResume(user, id, dto);
        return ResponseEntity.ok(ApiResponseWrapper.success("Resume autosaved successfully", resumeService.mapToDTO(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> deleteResume(@AuthenticationPrincipal User user, @PathVariable Long id) {
        resumeService.deleteResume(user, id);
        return ResponseEntity.ok(ApiResponseWrapper.success("Resume deleted successfully", null));
    }

    // PDF Resume download endpoint
    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> downloadPdf(@AuthenticationPrincipal User user, @PathVariable Long id) throws IOException {
        Resume resume = resumeService.getResumeById(user, id);
        byte[] pdfBytes = pdfExportService.exportToPdf(resume);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "resume-" + id + ".pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    // PDF File parsing autofill endpoint
    @PostMapping("/parse")
    public ResponseEntity<ApiResponseWrapper<ResumeDTO>> parseResumeFile(@RequestParam("file") MultipartFile file) throws IOException {
        ResumeDTO parsedDto = parserEngineService.parseResume(file);
        return ResponseEntity.ok(ApiResponseWrapper.success("Resume file parsed successfully", parsedDto));
    }
}
