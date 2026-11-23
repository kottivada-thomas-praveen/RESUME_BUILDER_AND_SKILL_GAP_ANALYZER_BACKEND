package com.thomas.rgsga.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAnalyticsDTO {
    private Map<String, Long> deviceTypes;
    private Map<String, Long> browsers;
    private Map<String, Long> operatingSystems;
    private Map<String, Long> topCountries;
}
