package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.CoverLetterRequest;
import com.thomas.rgsga.dto.CoverLetterResponse;
import com.thomas.rgsga.entity.User;

public interface CoverLetterService {
    CoverLetterResponse generateCoverLetter(User user, CoverLetterRequest request);
}
