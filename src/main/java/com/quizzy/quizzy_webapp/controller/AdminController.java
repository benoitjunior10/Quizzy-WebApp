package com.quizzy.quizzy_webapp.controller;

import com.quizzy.quizzy_webapp.dto.*;
import com.quizzy.quizzy_webapp.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Panneau d'administration (MVC server-side).
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminStatsService adminStatsService;
    private final AdminQuestionService adminQuestionService;
    private final AdminUserService adminUserService;
    private final CategoryService categoryService;

    @GetMapping
    public String dashboard(Model model) {
        try {
            AdminStatsDto stats = adminStatsService.getGlobalStats();
            model.addAttribute("stats", stats);
        } catch (Exception e) {
            log.error("Erreur chargement stats admin", e);
            model.addAttribute("error", "Impossible de charger les statistiques.");
        }
        return "admin/dashboard";
    }

    // ── Questions ──────────────────────────────────────────────
    @GetMapping("/questions")
    public String questionsPage(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Model model) {
        try {
            Page<QuestionResponseDto> questionsPage =
                    adminQuestionService.getAllQuestions(PageRequest.of(page, size));

            List<CategoryResponseDto> categories = categoryService.getAllCategoriesList();

            model.addAttribute("questions", questionsPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", questionsPage.getTotalPages());
            model.addAttribute("categories", categories);
        } catch (Exception e) {
            log.error("Erreur chargement questions admin", e);
            model.addAttribute("questions", List.of());
            model.addAttribute("error", "Impossible de charger les questions.");
        }
        return "admin/questions";
    }

    @PostMapping("/questions/create")
    public String createQuestion(@RequestParam String questionText,
                                 @RequestParam String difficulty,
                                 @RequestParam Long categoryId,
                                 @RequestParam List<String> optionTexts,
                                 @RequestParam(required = false, defaultValue = "") List<String> correctOptions) {
        try {
            QuestionRequestDto request = new QuestionRequestDto();
            request.setQuestionText(questionText);
            request.setDifficulty(difficulty);
            request.setCategoryId(categoryId);

            List<OptionDto> options = new ArrayList<>();
            for (int i = 0; i < optionTexts.size(); i++) {
                OptionDto opt = new OptionDto();
                opt.setOptionText(optionTexts.get(i));
                opt.setCorrect(correctOptions.contains(String.valueOf(i)));
                options.add(opt);
            }
            request.setOptions(options);

            adminQuestionService.createQuestion(request);
        } catch (Exception e) {
            log.error("Erreur création question", e);
            return "redirect:/admin/questions?error=create_failed";
        }
        return "redirect:/admin/questions?success=Question+créée";
    }

    @PostMapping("/questions/delete/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        try {
            adminQuestionService.deleteQuestion(id);
        } catch (Exception e) {
            log.error("Erreur suppression question {}", id, e);
            return "redirect:/admin/questions?error=delete_failed";
        }
        return "redirect:/admin/questions?success=Question+supprimée";
    }

    @PostMapping("/questions/update/{id}")
    public String updateQuestion(@PathVariable Long id,
                                 @RequestParam String questionText,
                                 @RequestParam String difficulty,
                                 @RequestParam Long categoryId) {
        try {
            QuestionRequestDto request = new QuestionRequestDto();
            request.setQuestionText(questionText);
            request.setDifficulty(difficulty);
            request.setCategoryId(categoryId);
            adminQuestionService.updateQuestion(id, request);
        } catch (Exception e) {
            log.error("Erreur modification question {}", id, e);
            return "redirect:/admin/questions?error=update_failed";
        }
        return "redirect:/admin/questions?success=Question+modifiée";
    }

    // ── Catégories ─────────────────────────────────────────────
    @GetMapping("/categories")
    public String categoriesPage(@RequestParam(defaultValue = "0") int page, Model model) {
        try {
            Page<CategoryResponseDto> categoriesPage =
                    categoryService.getAllCategories(PageRequest.of(page, 20));
            model.addAttribute("categories", categoriesPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", categoriesPage.getTotalPages());
        } catch (Exception e) {
            log.error("Erreur chargement catégories admin", e);
            model.addAttribute("categories", List.of());
        }
        return "admin/categories";
    }

    @PostMapping("/categories/create")
    public String createCategory(@RequestParam String name) {
        try {
            categoryService.createCategory(name);
        } catch (Exception e) {
            log.error("Erreur création catégorie", e);
            return "redirect:/admin/categories?error=create_failed";
        }
        return "redirect:/admin/categories?success=Catégorie+créée";
    }

    @PostMapping("/categories/update/{id}")
    public String updateCategory(@PathVariable Long id, @RequestParam String name) {
        try {
            categoryService.updateCategory(id, name);
        } catch (Exception e) {
            log.error("Erreur modification catégorie {}", id, e);
            return "redirect:/admin/categories?error=update_failed";
        }
        return "redirect:/admin/categories?success=Catégorie+modifiée";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
        } catch (Exception e) {
            log.error("Erreur suppression catégorie {}", id, e);
            return "redirect:/admin/categories?error=delete_failed";
        }
        return "redirect:/admin/categories?success=Catégorie+supprimée";
    }

    // ── Utilisateurs ───────────────────────────────────────────
    @GetMapping("/users")
    public String usersPage(@RequestParam(defaultValue = "0") int page,
                            Model model) {
        try {
            Page<UserResponseDto> usersPage = adminUserService.getAllUsers(PageRequest.of(page, 15));
            model.addAttribute("users", usersPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", usersPage.getTotalPages());
            model.addAttribute("totalElements", usersPage.getTotalElements());
        } catch (Exception e) {
            log.error("Erreur chargement utilisateurs admin", e);
            model.addAttribute("users", List.of());
        }
        return "admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        try {
            adminUserService.deleteUser(id);
        } catch (Exception e) {
            log.error("Erreur suppression utilisateur {}", id, e);
            return "redirect:/admin/users?error=delete_failed";
        }
        return "redirect:/admin/users?success=Utilisateur+supprimé";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String role) {
        try {
            UserUpdateRequest request = new UserUpdateRequest();
            request.setUsername(username);
            request.setEmail(email);
            request.setRole(com.quizzy.quizzy_webapp.model.Role.valueOf(role));
            adminUserService.updateUser(id, request);
        } catch (Exception e) {
            log.error("Erreur modification utilisateur {}", id, e);
            return "redirect:/admin/users?error=update_failed";
        }
        return "redirect:/admin/users?success=Utilisateur+modifié";
    }
}
