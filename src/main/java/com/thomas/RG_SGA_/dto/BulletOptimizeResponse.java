package com.thomas.RG_SGA_.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulletOptimizeResponse {
    private String original;
    private String optimized;
    private String explanation;
}
