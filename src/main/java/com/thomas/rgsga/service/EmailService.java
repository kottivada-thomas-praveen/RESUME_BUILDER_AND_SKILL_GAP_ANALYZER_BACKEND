package com.thomas.rgsga.service;

import com.thomas.rgsga.dto.ContactRequest;

public interface EmailService {
    void sendContactQuery(ContactRequest request);
}
