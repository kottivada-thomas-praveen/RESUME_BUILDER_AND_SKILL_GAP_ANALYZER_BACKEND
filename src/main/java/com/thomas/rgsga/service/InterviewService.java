package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.InterviewRequest;
import com.thomas.rgsga.dto.InterviewResponse;
import com.thomas.rgsga.entity.User;

public interface InterviewService {

    InterviewResponse generateInterviewPrep(User user, InterviewRequest request);
}
