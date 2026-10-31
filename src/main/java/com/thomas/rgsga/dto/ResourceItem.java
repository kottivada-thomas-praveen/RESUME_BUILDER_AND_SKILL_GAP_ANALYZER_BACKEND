package com.thomas.rgsga.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceItem {

    @JsonAlias({"name", "title"})
    private String title;

    private String provider;
    private String url;

    @JsonAlias({"type", "durationOrCost"})
    private String durationOrCost;

    private String description;
}
