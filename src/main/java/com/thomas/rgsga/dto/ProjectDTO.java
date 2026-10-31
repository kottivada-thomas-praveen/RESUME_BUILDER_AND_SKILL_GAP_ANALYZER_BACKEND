package com.thomas.rgsga.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDTO {

    private Long id;

    @JsonProperty("name")
    @JsonAlias({"title", "projectName", "project_name"})
    private String name;

    @JsonProperty("technologies")
    @JsonAlias({"tech", "techStack", "technologiesUsed", "tech_stack"})
    private String technologies;

    @JsonProperty("link")
    @JsonAlias({"url", "projectUrl", "project_url"})
    private String link;

    private String description;

    public void setGithubLink(String githubLink) {
        if (this.link == null || this.link.isBlank()) {
            this.link = githubLink;
        }
    }

    public void setLiveDemoLink(String liveDemoLink) {
        if (this.link == null || this.link.isBlank()) {
            this.link = liveDemoLink;
        }
    }
}
