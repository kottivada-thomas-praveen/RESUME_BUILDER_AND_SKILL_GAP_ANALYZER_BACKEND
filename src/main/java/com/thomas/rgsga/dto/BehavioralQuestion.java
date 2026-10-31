package com.thomas.rgsga.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehavioralQuestion {

    private String question;
    private String starGuidance;
    private String tips;
}
