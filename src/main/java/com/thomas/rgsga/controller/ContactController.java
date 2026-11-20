package com.thomas.rgsga.controller;

import com.thomas.rgsga.dto.ApiResponse;
import com.thomas.rgsga.dto.ContactRequest;
import com.thomas.rgsga.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
@Tag(name = "Contact", description = "Public user queries and contact form submission")
public class ContactController {

    private final EmailService emailService;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    @Operation(summary = "Submit a contact query delivered to administrator email")
    public ResponseEntity<ApiResponse<Void>> submitContactQuery(@Valid @RequestBody ContactRequest request) {
        emailService.sendContactQuery(request);
        return ResponseEntity.ok(ApiResponse.success(
                "Your inquiry has been submitted successfully and delivered to the administrator. We will reply to your email shortly.",
                null
        ));
    }
}
