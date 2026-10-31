package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.ExtractedKeywordsResponse;

public interface KeywordExtractionService {
    ExtractedKeywordsResponse extractKeywords(String jobDescription);
}
