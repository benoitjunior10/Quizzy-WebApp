package com.quizzy.quizzy_webapp.service;

import com.quizzy.quizzy_webapp.dto.*;
import com.quizzy.quizzy_webapp.exception.ResourceNotFoundException;
import com.quizzy.quizzy_webapp.model.*;
import com.quizzy.quizzy_webapp.repository.CategoryRepository;
import com.quizzy.quizzy_webapp.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminQuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;

    public Page<QuestionResponseDto> getAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable).map(this::mapToQuestionDto);
    }

    private void validateQuestionOptions(List<OptionDto> options) {
        if (options == null || options.size() < 3) {
            throw new IllegalArgumentException("Une question doit comporter au moins 3 options.");
        }

        long correctCount = options.stream().filter(OptionDto::isCorrect).count();
        if (correctCount == 0) {
            throw new IllegalArgumentException("La question doit avoir au moins une bonne réponse.");
        }
        // 1 seule bonne réponse (adapter si multi-réponse)
        if (correctCount != 1) {
            throw new IllegalArgumentException("La question doit avoir exactement une seule bonne réponse.");
        }
    }

    @Transactional
    public QuestionResponseDto createQuestion(QuestionRequestDto dto) {
        validateQuestionOptions(dto.getOptions());

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable"));

        Question question = Question.builder()
                .questionText(dto.getQuestionText())
                .difficulty(dto.getDifficulty())
                .category(category)
                .build();

        for (OptionDto opt : dto.getOptions()) {
            question.addOption(Option.builder()
                    .optionText(opt.getOptionText())
                    .correct(opt.isCorrect())
                    .build());
        }

        return mapToQuestionDto(questionRepository.save(question));
    }

    /**
     * Mise à jour :
     * - si dto.options est fourni → remplace les options
     * - sinon → ne touche pas aux options existantes
     */
    @Transactional
    public QuestionResponseDto updateQuestion(Long id, QuestionRequestDto dto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question introuvable avec l'ID: " + id));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'ID: " + dto.getCategoryId()));

        question.setQuestionText(dto.getQuestionText());
        question.setDifficulty(dto.getDifficulty());
        question.setCategory(category);

        boolean hasOptions = dto.getOptions() != null && !dto.getOptions().isEmpty();
        if (hasOptions) {
            validateQuestionOptions(dto.getOptions());
            // orphanRemoval=true va supprimer les anciennes options en base
            question.getOptions().clear();
            for (OptionDto opt : dto.getOptions()) {
                question.addOption(Option.builder()
                        .optionText(opt.getOptionText())
                        .correct(opt.isCorrect())
                        .build());
            }
        }

        return mapToQuestionDto(questionRepository.save(question));
    }

    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question introuvable avec l'ID: " + id);
        }
        questionRepository.deleteById(id);
    }

    private QuestionResponseDto mapToQuestionDto(Question q) {
        CategoryResponseDto categoryDto = q.getCategory() != null
                ? CategoryResponseDto.builder().id(q.getCategory().getId()).name(q.getCategory().getName()).build()
                : null;

        List<OptionResponseDto> optionsDto = q.getOptions().stream()
                .map(opt -> OptionResponseDto.builder()
                        .id(opt.getId())
                        .optionText(opt.getOptionText())
                        .correct(opt.isCorrect())
                        .build())
                .toList();

        return QuestionResponseDto.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .difficulty(q.getDifficulty())
                .category(categoryDto)
                .options(optionsDto)
                .build();
    }
}
