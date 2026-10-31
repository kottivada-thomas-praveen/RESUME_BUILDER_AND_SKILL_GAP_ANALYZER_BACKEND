package com.thomas.rgsga.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    private String bio;
    private String githubUrl;
    private String linkedinUrl;
    private String twitterUrl;
}
