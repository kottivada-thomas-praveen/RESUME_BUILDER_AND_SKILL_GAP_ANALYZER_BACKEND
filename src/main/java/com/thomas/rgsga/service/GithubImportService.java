package com.thomas.rgsga.service;

import com.thomas.rgsga.entity.GithubAnalysis;
import com.thomas.rgsga.entity.User;

public interface GithubImportService {

    GithubAnalysis analyzeGithubProfile(User user, String username);
}
