package com.quizzy.quizzy_webapp.controller;

import com.quizzy.quizzy_webapp.dto.*;
import com.quizzy.quizzy_webapp.service.CategoryService;
import com.quizzy.quizzy_webapp.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Déroulement d'un quiz.
 */
@Controller
@RequestMapping("/quiz")
@RequiredArgsConstructor
@Slf4j
public class QuizController {

    private final QuizService quizService;
    private final CategoryService categoryService;

    @GetMapping("/play/{categoryId}")
    public String playQuiz(@PathVariable Long categoryId, Model model) {
        try {
            List<QuestionPublicDto> questions = quizService.getQuestionsByCategory(categoryId);

            String categoryName = categoryService.getAllCategoriesList().stream()
                    .filter(c -> c.getId().equals(categoryId))
                    .findFirst()
                    .map(CategoryResponseDto::getName)
                    .orElse("Quiz");

            model.addAttribute("questions", questions);
            model.addAttribute("categoryId", categoryId);
            model.addAttribute("categoryName", categoryName);
            model.addAttribute("totalQuestions", questions.size());
        } catch (Exception e) {
            log.error("Erreur chargement quiz catégorie {}", categoryId, e);
            model.addAttribute("error", "Impossible de charger ce quiz.");
            return "error";
        }

        return "quiz/play";
    }

    @PostMapping("/submit")
    public String submitQuiz(@RequestParam Long categoryId,
                             @RequestParam(required = false) List<Long> selectedOptionIds,
                             @RequestParam int duration,
                             Authentication authentication,
                             Model model) {
        try {
            QuizSubmitDto submitDto = new QuizSubmitDto();
            submitDto.setCategoryId(categoryId);
            submitDto.setSelectedOptionIds(selectedOptionIds != null ? selectedOptionIds : List.of());
            submitDto.setDuration(duration);

            // QuizService lit l'utilisateur dans le SecurityContext
            QuizAttemptResponseDto result = quizService.submitQuiz(submitDto);
            model.addAttribute("result", result);

        } catch (Exception e) {
            log.error("Erreur soumission quiz", e);
            model.addAttribute("error", "Erreur lors de la soumission du quiz.");
            return "error";
        }

        return "quiz/result";
    }
}
