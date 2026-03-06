package com.quizzy.quizzy_webapp.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptResponseDto {
    private Long id;
    /**
     * Score du quiz (en pourcentage). Exemple: 70.0 signifie 70%.
     */
    private double score;
    private int correctAnswers;
    private int totalQuestions;
    private int duration;
    private LocalDateTime date;
    private String username;
    private String categoryName;
}
