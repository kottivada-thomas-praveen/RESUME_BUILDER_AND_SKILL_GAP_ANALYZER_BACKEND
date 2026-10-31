package com.thomas.rgsga.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapStep {

    @JsonAlias({"stepNumber", "step"})
    private Integer stepNumber;

    private String title;
    private String description;

    @JsonAlias({"duration", "timeline"})
    private String timeline;

    @Builder.Default
    private List<String> topics = new ArrayList<>();
}
