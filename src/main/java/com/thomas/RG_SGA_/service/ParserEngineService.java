package com.thomas.RG_SGA_.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.RG_SGA_.dto.ResumeDTO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class ParserEngineService {

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    public ParserEngineService(GeminiService geminiService, ObjectMapper objectMapper) {
        this.geminiService = geminiService;
        this.objectMapper = objectMapper;
    }

    /**
     * Extracts text from an uploaded file (PDF or TXT) and parses it using Gemini AI.
     */
    public ResumeDTO parseResume(MultipartFile file) throws IOException {
        String extractedText = extractText(file);
        
        String systemInstruction = "You are a professional resume parsing AI. Parse the following unstructured resume text " +
                "into a highly structured JSON matching the ResumeDTO schema. Extract all skills, work experiences, education history, and personal details. " +
                "Do not make up information; only extract what is in the text. " +
                "Strictly return ONLY the JSON payload, matching this exact schema:\n" +
                "{\n" +
                "  \"fullName\": \"\",\n" +
                "  \"title\": \"\",\n" +
                "  \"email\": \"\",\n" +
                "  \"phone\": \"\",\n" +
                "  \"location\": \"\",\n" +
                "  \"website\": \"\",\n" +
                "  \"summary\": \"\",\n" +
                "  \"skills\": \"comma, separated, skills\",\n" +
                "  \"experiences\": [\n" +
                "    { \"company\": \"\", \"role\": \"\", \"startDate\": \"\", \"endDate\": \"\", \"location\": \"\", \"description\": \"\" }\n" +
                "  ],\n" +
                "  \"educations\": [\n" +
                "    { \"school\": \"\", \"degree\": \"\", \"field\": \"\", \"gradDate\": \"\", \"location\": \"\" }\n" +
                "  ],\n" +
                "  \"projects\": [\n" +
                "    { \"name\": \"\", \"technologies\": \"\", \"link\": \"\", \"description\": \"\" }\n" +
                "  ]\n" +
                "}";

        String parsedJson = geminiService.generateJsonContent(systemInstruction, extractedText);
        
        try {
            return objectMapper.readValue(parsedJson, ResumeDTO.class);
        } catch (Exception e) {
            System.err.println("Failed to parse JSON returned by Gemini: " + e.getMessage());
            // Return parsed fallback as fallback DTO
            return objectMapper.readValue(geminiService.generateJsonContent("", "demo_general_resume_parsing"), ResumeDTO.class);
        }
    }

    /**
     * Helper to extract raw text based on file format.
     */
    public String extractText(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("Invalid file name");
        }

        if (filename.toLowerCase().endsWith(".pdf")) {
            return extractTextFromPdf(file.getInputStream());
        } else if (filename.toLowerCase().endsWith(".txt")) {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } else {
            throw new IllegalArgumentException("Unsupported file type. Please upload a PDF or TXT file.");
        }
    }

    private String extractTextFromPdf(InputStream inputStream) throws IOException {
        try (PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (Exception e) {
            throw new IOException("Failed to parse PDF document: " + e.getMessage(), e);
        }
    }
}
