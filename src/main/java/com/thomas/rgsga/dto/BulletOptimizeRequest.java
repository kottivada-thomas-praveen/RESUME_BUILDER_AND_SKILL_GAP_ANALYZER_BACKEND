package com.thomas.rgsga.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BulletOptimizeRequest {

    @NotBlank(message = "Bullet point text is required")
    @JsonProperty("bulletPoint")
    @JsonAlias({"text", "bullet", "content", "sentence", "bullet_point", "bulletText"})
    private String bulletPoint;

    @JsonProperty("jobDescription")
    @JsonAlias({"jobDesc", "targetJobDescription", "job", "description", "job_description", "jobText"})
    private String jobDescription;

    public void setText(String text) {
        if (text != null && !text.isBlank()) {
            this.bulletPoint = text;
        }
    }

    public void setBullet(String bullet) {
        if (bullet != null && !bullet.isBlank()) {
            this.bulletPoint = bullet;
        }
    }

    public void setContent(String content) {
        if (content != null && !content.isBlank()) {
            this.bulletPoint = content;
        }
    }

    public void setJobDesc(String jobDesc) {
        if (jobDesc != null && !jobDesc.isBlank()) {
            this.jobDescription = jobDesc;
        }
    }

    public void setTargetJobDescription(String targetJobDescription) {
        if (targetJobDescription != null && !targetJobDescription.isBlank()) {
            this.jobDescription = targetJobDescription;
        }
    }

    public void setJobText(String jobText) {
        if (jobText != null && !jobText.isBlank()) {
            this.jobDescription = jobText;
        }
    }
}
