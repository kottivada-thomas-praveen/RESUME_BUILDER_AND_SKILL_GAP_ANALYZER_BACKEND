package com.thomas.rgsga.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulletOptimizeResponse {

    private String original;
    private String optimized;
    private String explanation;
}
