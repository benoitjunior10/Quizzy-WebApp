/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quizzy.quizzy_webapp.service;

import com.quizzy.quizzy_webapp.dto.CategoryResponseDto;
import com.quizzy.quizzy_webapp.exception.ResourceNotFoundException;

/**
 *
 * @author HP
 */

import com.quizzy.quizzy_webapp.model.Category;
import com.quizzy.quizzy_webapp.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }

    public Page<CategoryResponseDto> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(this::mapToDto);
    }

    /**
     * Utilisé par les pages web (Thymeleaf) pour alimenter les listes déroulantes.
     */
    public List<CategoryResponseDto> getAllCategoriesList() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public CategoryResponseDto updateCategory(Long id, String newName) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'ID: " + id));
        category.setName(newName);
        return mapToDto(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Catégorie introuvable avec l'ID: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryResponseDto mapToDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}