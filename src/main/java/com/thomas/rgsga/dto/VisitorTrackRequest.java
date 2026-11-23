package com.thomas.rgsga.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorTrackRequest {

    @NotBlank(message = "visitorId is required")
    private String visitorId;

    private String sessionId;

    @NotBlank(message = "pageUrl is required")
    private String pageUrl;

    private String pageTitle;
    private String referrerUrl;
    private String deviceType;
    private String browser;
    private String os;
    private String country;
    private String city;
    private Integer durationSeconds;
}
