package com.thomas.rgsga.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GrammarCheckRequest {

    @NotBlank(message = "Text for grammar check cannot be blank")
    @JsonProperty("text")
    @JsonAlias({"bulletPoint", "content", "bullet", "sentence", "bullet_point"})
    private String text;

    public void setBulletPoint(String bulletPoint) {
        if (bulletPoint != null && !bulletPoint.isBlank()) {
            this.text = bulletPoint;
        }
    }

    public void setContent(String content) {
        if (content != null && !content.isBlank()) {
            this.text = content;
        }
    }

    public void setBullet(String bullet) {
        if (bullet != null && !bullet.isBlank()) {
            this.text = bullet;
        }
    }
}
