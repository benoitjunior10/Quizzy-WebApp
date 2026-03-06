package com.quizzy.quizzy_webapp.controller;

import com.quizzy.quizzy_webapp.dto.RegisterRequest;
import com.quizzy.quizzy_webapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Pages d'authentification.
 *
 * NB: Le POST /auth/login est géré par Spring Security (formLogin).
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String message,
                            @RequestParam(required = false) String redirect,
                            Authentication authentication,
                            Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        // Le template affiche ${error} / ${message}
        if (error != null) {
            model.addAttribute("error", "Identifiants incorrects. Veuillez réessayer.");
        }
        model.addAttribute("message", message);
        model.addAttribute("redirect", redirect);
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             Model model) {
        try {
            RegisterRequest request = new RegisterRequest();
            request.setUsername(username);
            request.setEmail(email);
            request.setPassword(password);
            authService.register(request);
            return "redirect:/auth/login?message=Compte+créé+avec+succès+!+Connectez-vous.";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        } catch (Exception e) {
            log.error("Erreur d'inscription", e);
            model.addAttribute("error", "Erreur lors de l'inscription. Réessayez.");
            return "auth/register";
        }
    }
}
