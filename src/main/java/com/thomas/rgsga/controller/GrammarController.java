package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.dto.GrammarCheckRequest;
import com.thomas.rgsga.dto.GrammarCheckResponse;
import com.thomas.rgsga.service.GrammarCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/grammar")
@Tag(name = "Grammar & Spelling", description = "Integration with LanguageTool to check grammar and typos in resume bullet points")
public class GrammarController {

    private final GrammarCheckService grammarCheckService;

    public GrammarController(GrammarCheckService grammarCheckService) {
        this.grammarCheckService = grammarCheckService;
    }

    @PostMapping("/check")
    @Operation(summary = "Check bullet point text for typos and grammatical errors")
    public ResponseEntity<ApiResponse<GrammarCheckResponse>> checkGrammar(@Valid @RequestBody GrammarCheckRequest request) {
        GrammarCheckResponse response = grammarCheckService.checkGrammar(request.getText());
        return ResponseEntity.ok(ApiResponse.success("Grammar analysis completed", response));
    }
}
