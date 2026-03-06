package com.quizzy.quizzy_webapp.dto;
import lombok.Data;
import java.util.List;
@Data
public class QuestionPublicDto {
    private Long id;
    private String questionText;
    private String difficulty;
    private List<OptionPublicDto> options;
}
