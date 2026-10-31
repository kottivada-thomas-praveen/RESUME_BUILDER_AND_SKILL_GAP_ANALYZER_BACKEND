package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.AtsRequest;
import com.thomas.rgsga.dto.AtsResponse;
import com.thomas.rgsga.entity.User;

public interface AtsService {

    AtsResponse analyzeResume(User user, AtsRequest request);
}
