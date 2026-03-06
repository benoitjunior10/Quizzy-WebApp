package com.quizzy.quizzy_webapp.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class OptionDto {
    private String optionText;
    @JsonProperty("isCorrect")
    private boolean correct;
}
