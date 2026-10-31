package com.thomas.rgsga.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleLoginRequest {

    @JsonAlias({"idToken", "token"})
    private String credential;

    private String email;
    private String name;
    private String googleId;
}
