package com.thomas.rgsga.service;

import java.io.IOException;

public interface FileStorageService {
    String storeResumePdf(Long resumeId, byte[] pdfBytes) throws IOException;
    byte[] loadResumePdf(String filename) throws IOException;
    boolean deleteResumePdf(String filename);
    void deleteUserFiles(Long userId);
}
