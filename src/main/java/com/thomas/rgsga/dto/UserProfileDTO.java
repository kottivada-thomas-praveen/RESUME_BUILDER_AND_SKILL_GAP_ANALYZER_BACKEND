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
public class UserProfileDTO {

    private String bio;

    @JsonProperty("githubUrl")
    @JsonAlias({"github", "github_url"})
    private String githubUrl;

    @JsonProperty("linkedinUrl")
    @JsonAlias({"linkedin", "linkedin_url"})
    private String linkedinUrl;

    @JsonProperty("twitterUrl")
    @JsonAlias({"twitter", "twitter_url"})
    private String twitterUrl;

    @JsonProperty("portfolioUrl")
    @JsonAlias({"portfolio", "website", "portfolio_url"})
    private String portfolioUrl;

    public void setGithub(String github) {
        if (github != null && !github.isBlank()) {
            this.githubUrl = github;
        }
    }

    public void setLinkedin(String linkedin) {
        if (linkedin != null && !linkedin.isBlank()) {
            this.linkedinUrl = linkedin;
        }
    }

    public void setTwitter(String twitter) {
        if (twitter != null && !twitter.isBlank()) {
            this.twitterUrl = twitter;
        }
    }

    public void setPortfolio(String portfolio) {
        if (portfolio != null && !portfolio.isBlank()) {
            this.portfolioUrl = portfolio;
        }
    }

    public void setWebsite(String website) {
        if (website != null && !website.isBlank()) {
            this.portfolioUrl = website;
        }
    }
}
