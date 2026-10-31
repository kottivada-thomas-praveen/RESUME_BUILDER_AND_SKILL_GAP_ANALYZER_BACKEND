package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.GrammarCheckResponse;

public interface GrammarCheckService {
    GrammarCheckResponse checkGrammar(String text);
}
