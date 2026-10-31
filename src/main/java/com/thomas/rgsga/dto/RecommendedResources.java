package com.thomas.rgsga.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedResources {

    @Builder.Default
    private List<ResourceItem> courses = new ArrayList<>();

    @Builder.Default
    private List<ResourceItem> youtubeVideos = new ArrayList<>();

    @Builder.Default
    private List<ResourceItem> certifications = new ArrayList<>();
}
