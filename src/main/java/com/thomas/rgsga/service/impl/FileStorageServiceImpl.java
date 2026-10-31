package com.thomas.rgsga.service.impl;

import com.thomas.rgsga.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path storageDirectory;

    public FileStorageServiceImpl(@Value("${application.storage.local-dir:uploads/resumes/pdf}") String storagePath) {
        this.storageDirectory = Paths.get(storagePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageDirectory);
            log.info("Initialized local PDF file storage at: {}", this.storageDirectory);
        } catch (IOException e) {
            log.error("Failed to initialize file storage directory: {}", e.getMessage());
        }
    }

    @Override
    public String storeResumePdf(Long resumeId, byte[] pdfBytes) throws IOException {
        String filename = "resume_" + resumeId + "_" + UUID.randomUUID().toString().substring(0, 8) + ".pdf";
        Path targetPath = this.storageDirectory.resolve(filename);
        Files.write(targetPath, pdfBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        log.info("Temporarily cached PDF file at: {}", targetPath);
        return filename;
    }

    @Override
    public byte[] loadResumePdf(String filename) throws IOException {
        Path filePath = this.storageDirectory.resolve(filename).normalize();
        if (!Files.exists(filePath)) {
            throw new IOException("Stored PDF file not found: " + filename);
        }
        return Files.readAllBytes(filePath);
    }

    @Override
    public boolean deleteResumePdf(String filename) {
        try {
            Path filePath = this.storageDirectory.resolve(filename).normalize();
            return Files.deleteIfExists(filePath);
        } catch (Exception e) {
            log.warn("Could not delete stored PDF file {}: {}", filename, e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteUserFiles(Long userId) {
        try {
            File dir = storageDirectory.toFile();
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles((d, name) -> name.startsWith("resume_"));
                if (files != null) {
                    for (File f : files) {
                        try {
                            f.delete();
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error cleaning up user files: {}", e.getMessage());
        }
    }
}
