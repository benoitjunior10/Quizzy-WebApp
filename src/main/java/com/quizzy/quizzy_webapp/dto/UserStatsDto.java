package com.quizzy.quizzy_webapp.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDto {
    private int totalAttempts;
    private double averageScore;
    private double bestScore;
    private int totalTimeSpent;
}
