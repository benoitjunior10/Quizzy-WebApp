package com.quizzy.quizzy_webapp.controller;

import com.quizzy.quizzy_webapp.dto.CategoryResponseDto;
import com.quizzy.quizzy_webapp.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

/**
 * Page d'accueil : liste des catégories.
 */
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final CategoryService categoryService;

    @GetMapping
    public String home(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String error,
                       Model model) {
        try {
            Page<CategoryResponseDto> categories = categoryService.getAllCategories(PageRequest.of(page, 12));
            model.addAttribute("categories", categories.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", categories.getTotalPages());
            model.addAttribute("totalElements", categories.getTotalElements());
        } catch (Exception e) {
            log.error("Erreur chargement catégories", e);
            model.addAttribute("categories", Collections.emptyList());
            model.addAttribute("apiError", "Impossible de charger les catégories.");
        }
        model.addAttribute("error", error);
        return "home";
    }
}
