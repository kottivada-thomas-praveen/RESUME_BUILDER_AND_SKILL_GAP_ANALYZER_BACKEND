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
public class TechnicalQuestion {

    private String question;

    @Builder.Default
    private String difficulty = "Medium";

    @Builder.Default
    private List<String> expectedKeyPoints = new ArrayList<>();

    @JsonAlias({"sampleAnswer", "answerOutline"})
    private String sampleAnswer;
}
