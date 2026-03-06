/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quizzy.quizzy_webapp.service;

import com.quizzy.quizzy_webapp.dto.OptionPublicDto;
import com.quizzy.quizzy_webapp.dto.QuestionPublicDto;
import com.quizzy.quizzy_webapp.dto.QuizAttemptResponseDto;

/**
 *
 * @author HP
 */

import com.quizzy.quizzy_webapp.dto.QuizSubmitDto;
import com.quizzy.quizzy_webapp.dto.UserStatsDto;
import com.quizzy.quizzy_webapp.exception.ResourceNotFoundException;
import com.quizzy.quizzy_webapp.model.*;
import com.quizzy.quizzy_webapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public List<QuestionPublicDto> getQuestionsByCategory(Long categoryId) {
        List<Question> questions = questionRepository.findByCategoryId(categoryId);
        
        // Convertir les entités en DTOs (idéalement via MapStruct, ici fait manuellement pour l'exemple)
        return questions.stream().map(q -> {
            QuestionPublicDto dto = new QuestionPublicDto();
            dto.setId(q.getId());
            dto.setQuestionText(q.getQuestionText());
            dto.setDifficulty(q.getDifficulty());
            dto.setOptions(q.getOptions().stream().map(o -> {
                OptionPublicDto optDto = new OptionPublicDto();
                optDto.setId(o.getId());
                optDto.setOptionText(o.getOptionText());
                return optDto;
            }).toList());
            return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<QuizAttemptResponseDto> getUserAttempts(String username) {
        return quizAttemptRepository.findByUserUsernameOrderByDateDesc(username).stream().map(this::mapToAttemptDto).toList();
    }
    
    
    // @Transactional
    // public QuizAttemptResponseDto submitQuiz(QuizSubmitDto submitDto) {
    //     String username = SecurityContextHolder.getContext().getAuthentication().getName();
    //     User user = userRepository.findByUsername(username)
    //             .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

    //     Category category = categoryRepository.findById(submitDto.getCategoryId())
    //             .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable"));

    //     // Eliminer les éventuels doublons d'IDs envoyés par le client (ex: [10, 10] devient [10])
    //     Set<Long> uniqueOptionIds = new HashSet<>(submitDto.getSelectedOptionIds());

    //     List<Option> selectedOptions = optionRepository.findAllById(uniqueOptionIds);
        
    //     // Vérifier que tous les IDs existent bien en base
    //     if (selectedOptions.size() != uniqueOptionIds.size()) {
    //         throw new IllegalArgumentException("Certaines options soumises n'existent pas ou sont invalides.");
    //     }

    //     // ANTI-TRICHE : Vérifier qu'il n'y a pas plusieurs réponses pour une même question
    //     long distinctQuestionsCount = selectedOptions.stream()
    //             .map(option -> option.getQuestion().getId())
    //             .distinct()
    //             .count();

    //     if (distinctQuestionsCount < selectedOptions.size()) {
    //         throw new IllegalArgumentException("Triche détectée : vous avez soumis plusieurs options pour une même question.");
    //     }

    //     // Calcul du score
    //     int score = (int) selectedOptions.stream()
    //             .filter(Option::isCorrect)
    //             .count();

    //     // Enregistrement incluant la catégorie
    //     QuizAttempt attempt = QuizAttempt.builder()
    //             .score(score)
    //             .duration(submitDto.getDuration())
    //             .date(LocalDateTime.now())
    //             .user(user)
    //             .category(category)
    //             .build();

    //     return mapToAttemptDto(quizAttemptRepository.save(attempt));
    // }


    // private QuizAttemptResponseDto mapToAttemptDto(QuizAttempt attempt) {
    //     return QuizAttemptResponseDto.builder()
    //             .id(attempt.getId())
    //             .score(attempt.getScore())
    //             .duration(attempt.getDuration())
    //             .date(attempt.getDate())
    //             .username(attempt.getUser().getUsername())
    //             .categoryName(attempt.getCategory() != null ? attempt.getCategory().getName() : "Inconnue")
    //             .build();
    // }


    // @Transactional(readOnly = true)
    // public UserStatsDto getUserStats(String username) {
    //     List<QuizAttempt> attempts = quizAttemptRepository.findByUserUsernameOrderByDateDesc(username);

    //     if (attempts.isEmpty()) {
    //         return UserStatsDto.builder()
    //                 .totalAttempts(0).averageScore(0.0).bestScore(0).totalTimeSpent(0)
    //                 .build();
    //     }

    //     int totalAttempts = attempts.size();
    //     // Calcul via les Streams Java
    //     double averageScore = attempts.stream().mapToInt(QuizAttempt::getScore).average().orElse(0.0);
    //     int bestScore = attempts.stream().mapToInt(QuizAttempt::getScore).max().orElse(0);
    //     int totalTimeSpent = attempts.stream().mapToInt(QuizAttempt::getDuration).sum();

    //     return UserStatsDto.builder()
    //             .totalAttempts(totalAttempts)
    //             .averageScore(Math.round(averageScore * 100.0) / 100.0) // Arrondi à 2 décimales
    //             .bestScore(bestScore)
    //             .totalTimeSpent(totalTimeSpent)
    //             .build();
    // }

    @Transactional
    public QuizAttemptResponseDto submitQuiz(QuizSubmitDto submitDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        Category category = categoryRepository.findById(submitDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable"));

        Set<Long> uniqueOptionIds = new HashSet<>(submitDto.getSelectedOptionIds());
        List<Option> selectedOptions = optionRepository.findAllById(uniqueOptionIds);
        
        if (selectedOptions.size() != uniqueOptionIds.size()) {
            throw new IllegalArgumentException("Certaines options soumises n'existent pas ou sont invalides.");
        }

        long distinctQuestionsCount = selectedOptions.stream()
                .map(option -> option.getQuestion().getId())
                .distinct()
                .count();

        if (distinctQuestionsCount < selectedOptions.size()) {
            throw new IllegalArgumentException("Triche détectée : vous avez soumis plusieurs options pour une même question.");
        }

        // --- NOUVEAU CALCUL DU SCORE ---
        int totalQuestions = questionRepository.countByCategoryId(category.getId());
        if (totalQuestions == 0) {
            throw new IllegalArgumentException("La catégorie ne contient aucune question.");
        }

        int correctAnswers = (int) selectedOptions.stream()
                .filter(Option::isCorrect)
                .count();

        // Calcul du score en pourcentage (ex: 7 / 10 * 100 = 70.0%)
        double score = ((double) correctAnswers / totalQuestions) * 100.0;
        score = Math.round(score * 100.0) / 100.0; // Arrondi à 2 décimales

        QuizAttempt attempt = QuizAttempt.builder()
                .score(score)
                .correctAnswers(correctAnswers)
                .totalQuestions(totalQuestions)
                .duration(submitDto.getDuration())
                .date(LocalDateTime.now())
                .user(user)
                .category(category)
                .build();

        return mapToAttemptDto(quizAttemptRepository.save(attempt));
    }

    private QuizAttemptResponseDto mapToAttemptDto(QuizAttempt attempt) {
        return QuizAttemptResponseDto.builder()
                .id(attempt.getId())
                .score(attempt.getScore())
                .correctAnswers(attempt.getCorrectAnswers()) // Nouveau
                .totalQuestions(attempt.getTotalQuestions()) // Nouveau
                .duration(attempt.getDuration())
                .date(attempt.getDate())
                .username(attempt.getUser().getUsername())
                .categoryName(attempt.getCategory() != null ? attempt.getCategory().getName() : "Inconnue")
                .build();
    }

    @Transactional(readOnly = true)
    public UserStatsDto getUserStats(String username) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserUsernameOrderByDateDesc(username);

        if (attempts.isEmpty()) {
            return UserStatsDto.builder()
                    .totalAttempts(0).averageScore(0.0).bestScore(0.0).totalTimeSpent(0)
                    .build();
        }

        int totalAttempts = attempts.size();
        
        // Modifié : mapToInt devient mapToDouble
        double averageScore = attempts.stream().mapToDouble(QuizAttempt::getScore).average().orElse(0.0);
        double bestScore = attempts.stream().mapToDouble(QuizAttempt::getScore).max().orElse(0.0);
        int totalTimeSpent = attempts.stream().mapToInt(QuizAttempt::getDuration).sum();

        return UserStatsDto.builder()
                .totalAttempts(totalAttempts)
                .averageScore(Math.round(averageScore * 100.0) / 100.0)
                .bestScore(Math.round(bestScore * 100.0) / 100.0)
                .totalTimeSpent(totalTimeSpent)
                .build();
    }
}
