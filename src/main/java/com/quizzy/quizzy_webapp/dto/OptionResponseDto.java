package com.quizzy.quizzy_webapp.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionResponseDto {
    private Long id;
    private String optionText;
    @JsonProperty("isCorrect")
    private boolean correct;
}
