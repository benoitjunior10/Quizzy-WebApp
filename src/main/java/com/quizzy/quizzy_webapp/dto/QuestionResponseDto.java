package com.quizzy.quizzy_webapp.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDto {
    private Long id;
    private String questionText;
    private String difficulty;
    private CategoryResponseDto category;
    private List<OptionResponseDto> options;
}
