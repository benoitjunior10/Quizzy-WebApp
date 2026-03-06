package com.quizzy.quizzy_webapp.controller;

import com.quizzy.quizzy_webapp.dto.QuizAttemptResponseDto;
import com.quizzy.quizzy_webapp.dto.UserProfileDto;
import com.quizzy.quizzy_webapp.dto.UserProfileUpdateDto;
import com.quizzy.quizzy_webapp.dto.UserStatsDto;
import com.quizzy.quizzy_webapp.service.AuthService;
import com.quizzy.quizzy_webapp.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Profil + historique.
 */
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final AuthService authService;
    private final QuizService quizService;

    @GetMapping("/profile")
    public String profilePage(Authentication auth, Model model) {
        try {
            String username = auth.getName();
            UserProfileDto profile = authService.getCurrentUserProfile(username);
            UserStatsDto stats = quizService.getUserStats(username);
            model.addAttribute("profile", profile);
            model.addAttribute("stats", stats);
        } catch (Exception e) {
            log.error("Erreur chargement profil", e);
            model.addAttribute("error", "Impossible de charger le profil.");
        }
        return "user/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam(required = false) String username,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String oldPassword,
                                @RequestParam(required = false) String newPassword,
                                Authentication auth,
                                Model model) {
        try {
            UserProfileUpdateDto updateDto = new UserProfileUpdateDto();
            updateDto.setUsername(username);
            updateDto.setEmail(email);
            updateDto.setOldPassword(oldPassword);
            updateDto.setNewPassword(newPassword);

            authService.updateCurrentUser(auth.getName(), updateDto);
            return "redirect:/user/profile?success=Profil+mis+à+jour+avec+succès";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return profilePage(auth, model);
        } catch (Exception e) {
            log.error("Erreur mise à jour profil", e);
            model.addAttribute("error", "Erreur lors de la mise à jour du profil.");
            return profilePage(auth, model);
        }
    }

    @GetMapping("/history")
    public String historyPage(Authentication auth, Model model) {
        try {
            String username = auth.getName();
            List<QuizAttemptResponseDto> attempts = quizService.getUserAttempts(username);
            UserStatsDto stats = quizService.getUserStats(username);
            model.addAttribute("attempts", attempts);
            model.addAttribute("stats", stats);
        } catch (Exception e) {
            log.error("Erreur chargement historique", e);
            model.addAttribute("attempts", List.of());
            model.addAttribute("error", "Impossible de charger l'historique.");
        }
        return "user/history";
    }
}
