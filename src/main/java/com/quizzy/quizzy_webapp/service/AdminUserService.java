/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quizzy.quizzy_webapp.service;

import com.quizzy.quizzy_webapp.dto.QuizAttemptResponseDto;
import com.quizzy.quizzy_webapp.dto.UserResponseDto;
import com.quizzy.quizzy_webapp.dto.UserUpdateRequest;
import com.quizzy.quizzy_webapp.exception.ResourceNotFoundException;
import com.quizzy.quizzy_webapp.model.QuizAttempt;
import com.quizzy.quizzy_webapp.model.User;
import com.quizzy.quizzy_webapp.repository.QuizAttemptRepository;
import com.quizzy.quizzy_webapp.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
/**
 *
 * @author HP
 */
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToUserDto);
    }

    public List<QuizAttemptResponseDto> getUserAttempts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'ID: " + userId));
        return quizAttemptRepository.findByUserUsernameOrderByDateDesc(user.getUsername())
                .stream().map(this::mapToAttemptDto).toList();
    }


    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'ID: " + id));

        // Vérification unicité pseudo/email (si modifiés)
        userRepository.findByUsername(request.getUsername())
                .ifPresent(u -> { if(!u.getId().equals(id)) throw new IllegalArgumentException("Pseudo déjà utilisé"); });
        
        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> { if(!u.getId().equals(id)) throw new IllegalArgumentException("Email déjà utilisé"); });

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        // Si un nouveau mot de passe est fourni, on le hash
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return mapToUserDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur introuvable avec l'ID: " + id);
        }
        userRepository.deleteById(id);
    }


    private UserResponseDto mapToUserDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
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

}

