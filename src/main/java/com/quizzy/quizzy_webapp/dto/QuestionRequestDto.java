package com.quizzy.quizzy_webapp.dto;
import lombok.Data;
import java.util.List;
@Data
public class QuestionRequestDto {
    private String questionText;
    private String difficulty;
    private Long categoryId;
    private List<OptionDto> options;
}
