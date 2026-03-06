/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quizzy.quizzy_webapp.service;

/**
 *
 * @author HP
 */
import com.quizzy.quizzy_webapp.dto.AdminStatsDto;
import com.quizzy.quizzy_webapp.repository.CategoryRepository;
import com.quizzy.quizzy_webapp.repository.QuestionRepository;
import com.quizzy.quizzy_webapp.repository.QuizAttemptRepository;
import com.quizzy.quizzy_webapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminStatsService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    @Transactional(readOnly = true)
    public AdminStatsDto getGlobalStats() {
        Double avgScore = quizAttemptRepository.getGlobalAverageScore();

        return AdminStatsDto.builder()
                .totalUsers(userRepository.count())
                .totalCategories(categoryRepository.count())
                .totalQuestions(questionRepository.count())
                .totalQuizAttempts(quizAttemptRepository.count())
                // Si la BDD est vide, avgScore sera null, on le gère avec une ternaire
                .globalAverageScore(avgScore != null ? Math.round(avgScore * 100.0) / 100.0 : 0.0)
                .build();
    }
}


