package com.quizzy.quizzy_webapp.dto;
import lombok.Data;
import java.util.List;
@Data
public class QuizSubmitDto {
    private List<Long> selectedOptionIds;
    private int duration;
    private Long categoryId;
}
