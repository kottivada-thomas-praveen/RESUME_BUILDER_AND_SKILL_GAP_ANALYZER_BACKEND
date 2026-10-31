package com.thomas.rgsga.service;

import com.thomas.rgsga.entity.Education;
import com.thomas.rgsga.entity.Experience;
import com.thomas.rgsga.entity.Project;
import com.thomas.rgsga.entity.Resume;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PdfExportService {

    public byte[] exportToPdf(Resume resume) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font fontItalic = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

            float margin = 50;
            float width = page.getMediaBox().getWidth() - (2 * margin);
            float y = page.getMediaBox().getHeight() - margin;

            try (PDPageContentStream stream = new PDPageContentStream(document, page)) {

                // 1. Header (Name)
                String name = resume.getFullName() != null && !resume.getFullName().isBlank() ? resume.getFullName().toUpperCase() : "NAME";
                stream.beginText();
                stream.setFont(fontBold, 20);
                stream.newLineAtOffset(margin, y);
                stream.showText(clean(name));
                stream.endText();
                y -= 22;

                // Title
                if (resume.getTitle() != null && !resume.getTitle().isBlank()) {
                    stream.beginText();
                    stream.setFont(fontItalic, 12);
                    stream.newLineAtOffset(margin, y);
                    stream.showText(clean(resume.getTitle()));
                    stream.endText();
                    y -= 18;
                }

                // Contact line
                List<String> contactParts = new ArrayList<>();
                if (resume.getEmail() != null && !resume.getEmail().isBlank()) contactParts.add(resume.getEmail());
                if (resume.getPhone() != null && !resume.getPhone().isBlank()) contactParts.add(resume.getPhone());
                if (resume.getLocation() != null && !resume.getLocation().isBlank()) contactParts.add(resume.getLocation());
                if (resume.getWebsite() != null && !resume.getWebsite().isBlank()) contactParts.add(resume.getWebsite());

                if (!contactParts.isEmpty()) {
                    stream.beginText();
                    stream.setFont(fontRegular, 9);
                    stream.newLineAtOffset(margin, y);
                    stream.showText(clean(String.join("  |  ", contactParts)));
                    stream.endText();
                    y -= 14;
                }

                // Separator rule
                stream.setLineWidth(0.8f);
                stream.moveTo(margin, y);
                stream.lineTo(margin + width, y);
                stream.stroke();
                y -= 16;

                // 2. Professional Summary
                if (resume.getSummary() != null && !resume.getSummary().isBlank()) {
                    y = drawSectionHeader(stream, fontBold, margin, y, "PROFESSIONAL SUMMARY");
                    y = drawParagraph(stream, fontRegular, 9.5f, margin, y, width, clean(resume.getSummary()));
                    y -= 10;
                }

                // 3. Technical Skills
                if (resume.getSkillsCsv() != null && !resume.getSkillsCsv().isBlank()) {
                    y = drawSectionHeader(stream, fontBold, margin, y, "TECHNICAL SKILLS");
                    y = drawParagraph(stream, fontRegular, 9.5f, margin, y, width, clean(resume.getSkillsCsv()));
                    y -= 10;
                }

                // 4. Work Experience
                if (resume.getExperiences() != null && !resume.getExperiences().isEmpty()) {
                    y = drawSectionHeader(stream, fontBold, margin, y, "WORK EXPERIENCE");
                    for (Experience exp : resume.getExperiences()) {
                        if (y < margin + 40) break;

                        // Company & Role Header
                        stream.beginText();
                        stream.setFont(fontBold, 10);
                        stream.newLineAtOffset(margin, y);
                        String roleCompany = (exp.getRole() != null ? exp.getRole() : "") +
                                (exp.getCompany() != null ? " - " + exp.getCompany() : "");
                        stream.showText(clean(roleCompany));
                        stream.endText();

                        String dates = (exp.getStartDate() != null ? exp.getStartDate() : "") +
                                (exp.getEndDate() != null ? " to " + exp.getEndDate() : "");
                        if (!dates.isBlank()) {
                            stream.beginText();
                            stream.setFont(fontItalic, 9);
                            stream.newLineAtOffset(margin + width - 110, y);
                            stream.showText(clean(dates));
                            stream.endText();
                        }
                        y -= 14;

                        if (exp.getDescription() != null && !exp.getDescription().isBlank()) {
                            y = drawParagraph(stream, fontRegular, 9, margin + 8, y, width - 8, clean(exp.getDescription()));
                        }
                        y -= 6;
                    }
                    y -= 6;
                }

                // 5. Education
                if (resume.getEducations() != null && !resume.getEducations().isEmpty() && y > margin + 50) {
                    y = drawSectionHeader(stream, fontBold, margin, y, "EDUCATION");
                    for (Education edu : resume.getEducations()) {
                        if (y < margin + 30) break;
                        stream.beginText();
                        stream.setFont(fontBold, 9.5f);
                        stream.newLineAtOffset(margin, y);
                        String eduStr = (edu.getDegree() != null ? edu.getDegree() : "") +
                                (edu.getField() != null ? " in " + edu.getField() : "") +
                                (edu.getSchool() != null ? ", " + edu.getSchool() : "");
                        stream.showText(clean(eduStr));
                        stream.endText();
                        y -= 14;
                    }
                    y -= 6;
                }

                // 6. Projects
                if (resume.getProjects() != null && !resume.getProjects().isEmpty() && y > margin + 50) {
                    y = drawSectionHeader(stream, fontBold, margin, y, "PROJECTS");
                    for (Project proj : resume.getProjects()) {
                        if (y < margin + 30) break;
                        stream.beginText();
                        stream.setFont(fontBold, 9.5f);
                        stream.newLineAtOffset(margin, y);
                        String projHeader = (proj.getName() != null ? proj.getName() : "") +
                                (proj.getTechnologies() != null ? " (" + proj.getTechnologies() + ")" : "");
                        stream.showText(clean(projHeader));
                        stream.endText();
                        y -= 13;

                        if (proj.getDescription() != null && !proj.getDescription().isBlank()) {
                            y = drawParagraph(stream, fontRegular, 9, margin + 8, y, width - 8, clean(proj.getDescription()));
                        }
                        y -= 6;
                    }
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private float drawSectionHeader(PDPageContentStream stream, PDType1Font font, float x, float y, String title) throws IOException {
        stream.beginText();
        stream.setFont(font, 11);
        stream.newLineAtOffset(x, y);
        stream.showText(title);
        stream.endText();
        return y - 14;
    }

    private float drawParagraph(PDPageContentStream stream, PDType1Font font, float fontSize,
                                float x, float y, float maxWidth, String text) throws IOException {
        float leading = fontSize * 1.3f;
        List<String> lines = wrapText(text, font, fontSize, maxWidth);
        for (String line : lines) {
            if (y < 40) break; // prevent running off page
            stream.beginText();
            stream.setFont(font, fontSize);
            stream.newLineAtOffset(x, y);
            stream.showText(line);
            stream.endText();
            y -= leading;
        }
        return y;
    }

    private List<String> wrapText(String text, PDType1Font font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            float stringWidth = font.getStringWidth(clean(testLine)) / 1000 * fontSize;
            if (stringWidth <= maxWidth) {
                currentLine = new StringBuilder(testLine);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                }
                currentLine = new StringBuilder(word);
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    private String clean(String text) {
        if (text == null) return "";
        // Replace non-ASCII / non-printable characters for standard Type 1 Helvetica font safety
        return text.replaceAll("[^\\x20-\\x7E]", " ").trim();
    }
}
