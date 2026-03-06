package com.quizzy.quizzy_webapp.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDto {
    private long totalUsers;
    private long totalCategories;
    private long totalQuestions;
    private long totalQuizAttempts;
    private double globalAverageScore;
}
