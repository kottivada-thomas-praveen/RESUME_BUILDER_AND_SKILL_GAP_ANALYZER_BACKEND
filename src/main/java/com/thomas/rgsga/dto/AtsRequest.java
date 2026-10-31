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
public class AtsRequest {

    @JsonProperty("resumeId")
    @JsonAlias({"id", "resume_id"})
    private Long resumeId;

    @JsonProperty("resumeText")
    @JsonAlias({"resume", "text", "content", "resume_text"})
    private String resumeText;

    @NotBlank(message = "Job description is required")
    @JsonProperty("jobDescription")
    @JsonAlias({"jobDesc", "targetJobDescription", "job", "description", "job_description", "jobText"})
    private String jobDescription;

    public void setResume(String resume) {
        if (resume != null && !resume.isBlank()) {
            this.resumeText = resume;
        }
    }

    public void setText(String text) {
        if (text != null && !text.isBlank()) {
            this.resumeText = text;
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
