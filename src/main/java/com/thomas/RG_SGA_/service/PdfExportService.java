package com.thomas.RG_SGA_.service;

import com.thomas.RG_SGA_.entity.Resume;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfExportService {

    /**
     * Dynamically generates a premium, ATS-compliant PDF document for a given Resume entity.
     */
    public byte[] exportToPdf(Resume resume) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            // Fonts configuration
            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font fontItalic = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float width = page.getMediaBox().getWidth() - (2 * margin);
            float yPosition = yStart;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                
                // 1. Header (Full Name & Title)
                contentStream.beginText();
                contentStream.setFont(fontBold, 22);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(sanitizeText(resume.getFullName() != null ? resume.getFullName().toUpperCase() : "YOUR FULL NAME"));
                contentStream.endText();
                yPosition -= 24;

                if (resume.getTitle() != null && !resume.getTitle().isBlank()) {
                    contentStream.beginText();
                    contentStream.setFont(fontItalic, 13);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(sanitizeText(resume.getTitle()));
                    contentStream.endText();
                    yPosition -= 18;
                }

                // 2. Contact details line
                StringBuilder contact = new StringBuilder();
                if (resume.getEmail() != null) contact.append(resume.getEmail());
                if (resume.getPhone() != null) contact.append("  |  ").append(resume.getPhone());
                if (resume.getLocation() != null) contact.append("  |  ").append(resume.getLocation());
                if (resume.getWebsite() != null) contact.append("  |  ").append(resume.getWebsite());

                if (contact.length() > 0) {
                    contentStream.beginText();
                    contentStream.setFont(fontRegular, 9.5f);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(sanitizeText(contact.toString()));
                    contentStream.endText();
                    yPosition -= 12;
                }

                // Draw decorative line divider
                contentStream.setLineWidth(1.0f);
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(margin + width, yPosition);
                contentStream.stroke();
                yPosition -= 18;

                // 3. Professional Summary Section
                if (resume.getSummary() != null && !resume.getSummary().isBlank()) {
                    contentStream.beginText();
                    contentStream.setFont(fontBold, 12);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("PROFESSIONAL SUMMARY");
                    contentStream.endText();
                    yPosition -= 14;

                    String summary = resume.getSummary();
                    yPosition = ListLines(contentStream, fontRegular, 10, summary, margin, yPosition, width);
                    yPosition -= 18;
                }

                // 4. Work Experience Section
                if (resume.getExperiences() != null && !resume.getExperiences().isEmpty()) {
                    contentStream.beginText();
                    contentStream.setFont(fontBold, 12);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("PROFESSIONAL EXPERIENCE");
                    contentStream.endText();
                    yPosition -= 14;

                    for (var exp : resume.getExperiences()) {
                        // Company and Dates
                        contentStream.beginText();
                        contentStream.setFont(fontBold, 10.5f);
                        contentStream.newLineAtOffset(margin, yPosition);
                        contentStream.showText(sanitizeText(exp.getCompany() != null ? exp.getCompany() : "Company"));
                        contentStream.endText();

                        String dateStr = (exp.getStartDate() != null ? exp.getStartDate() : "") + 
                                " - " + (exp.getEndDate() != null ? exp.getEndDate() : "Present");
                        contentStream.beginText();
                        contentStream.setFont(fontRegular, 10);
                        float dateWidth = fontRegular.getStringWidth(sanitizeText(dateStr)) / 1000 * 10;
                        contentStream.newLineAtOffset(margin + width - dateWidth, yPosition);
                        contentStream.showText(sanitizeText(dateStr));
                        contentStream.endText();
                        yPosition -= 12;

                        // Job Title
                        contentStream.beginText();
                        contentStream.setFont(fontItalic, 10);
                        contentStream.newLineAtOffset(margin, yPosition);
                        contentStream.showText(sanitizeText(exp.getRole() != null ? exp.getRole() : "Role"));
                        contentStream.endText();
                        yPosition -= 12;

                        // Description
                        if (exp.getDescription() != null && !exp.getDescription().isBlank()) {
                            yPosition = ListLines(contentStream, fontRegular, 9.5f, exp.getDescription(), margin, yPosition, width);
                        }
                        yPosition -= 10;
                    }
                }

                // 5. Skills Section
                if (resume.getSkillsCsv() != null && !resume.getSkillsCsv().isBlank()) {
                    contentStream.beginText();
                    contentStream.setFont(fontBold, 12);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("CORE SKILLS");
                    contentStream.endText();
                    yPosition -= 14;

                    contentStream.beginText();
                    contentStream.setFont(fontRegular, 10);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(sanitizeText(resume.getSkillsCsv()));
                    contentStream.endText();
                    yPosition -= 24;
                }

                // 6. Education Section
                if (resume.getEducations() != null && !resume.getEducations().isEmpty()) {
                    contentStream.beginText();
                    contentStream.setFont(fontBold, 12);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("EDUCATION");
                    contentStream.endText();
                    yPosition -= 14;

                    for (var edu : resume.getEducations()) {
                        contentStream.beginText();
                        contentStream.setFont(fontBold, 10.5f);
                        contentStream.newLineAtOffset(margin, yPosition);
                        contentStream.showText(sanitizeText(edu.getSchool() != null ? edu.getSchool() : "University"));
                        contentStream.endText();

                        String gradDate = edu.getGradDate() != null ? edu.getGradDate() : "";
                        contentStream.beginText();
                        contentStream.setFont(fontRegular, 10);
                        float dateWidth = fontRegular.getStringWidth(sanitizeText(gradDate)) / 1000 * 10;
                        contentStream.newLineAtOffset(margin + width - dateWidth, yPosition);
                        contentStream.showText(sanitizeText(gradDate));
                        contentStream.endText();
                        yPosition -= 12;

                        contentStream.beginText();
                        contentStream.setFont(fontRegular, 10);
                        contentStream.newLineAtOffset(margin, yPosition);
                        contentStream.showText(sanitizeText((edu.getDegree() != null ? edu.getDegree() : "") + 
                                " in " + (edu.getField() != null ? edu.getField() : "")));
                        contentStream.endText();
                        yPosition -= 18;
                    }
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * Replaces smart quotes, non-breaking spaces, en-dashes, em-dashes and bullets with ASCII standard elements
     * to prevent PDFBox Standard 14 font encoding errors.
     */
    private String sanitizeText(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replace("\u00a0", " ") // non-breaking space
                .replace("’", "'")
                .replace("‘", "'")
                .replace("`", "'")
                .replace("“", "\"")
                .replace("”", "\"")
                .replace("–", "-") // en-dash
                .replace("—", "-") // em-dash
                .replace("•", "* ") // bullet point
                .replaceAll("[^\\x20-\\x7E\\n]", ""); // Keep standard printable ASCII characters
    }

    private float ListLines(PDPageContentStream contentStream, PDType1Font font, float fontSize, String text, 
                           float margin, float yPos, float width) throws IOException {
        String sanitizedText = sanitizeText(text);
        String[] words = sanitizedText.split("\\s+");
        StringBuilder line = new StringBuilder();
        float currentY = yPos;

        for (String word : words) {
            String testLine = line + word + " ";
            float testWidth = font.getStringWidth(testLine) / 1000 * fontSize;
            if (testWidth > width) {
                contentStream.beginText();
                contentStream.setFont(font, fontSize);
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText(line.toString().trim());
                contentStream.endText();
                line = new StringBuilder(word + " ");
                currentY -= (fontSize + 4);
            } else {
                line.append(word).append(" ");
            }
        }

        if (line.length() > 0) {
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(margin, currentY);
            contentStream.showText(line.toString().trim());
            contentStream.endText();
            currentY -= (fontSize + 4);
        }

        return currentY;
    }
}
