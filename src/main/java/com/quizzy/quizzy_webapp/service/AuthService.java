package com.quizzy.quizzy_webapp.service;

import com.quizzy.quizzy_webapp.dto.RegisterRequest;
import com.quizzy.quizzy_webapp.dto.UserProfileDto;
import com.quizzy.quizzy_webapp.dto.UserProfileUpdateDto;
import com.quizzy.quizzy_webapp.exception.ResourceNotFoundException;
import com.quizzy.quizzy_webapp.model.Role;
import com.quizzy.quizzy_webapp.model.User;
import com.quizzy.quizzy_webapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Services d'authentification côté application web.
 *
 * Ici, on utilise Spring Security (session + formLogin) — pas de JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Ce pseudo est déjà pris.");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserProfileDto getCurrentUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public UserProfileDto updateCurrentUser(String username, UserProfileUpdateDto request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        // Si champ non renseigné, on garde la valeur actuelle
        String newUsername = (request.getUsername() == null || request.getUsername().isBlank())
                ? user.getUsername() : request.getUsername();
        String newEmail = (request.getEmail() == null || request.getEmail().isBlank())
                ? user.getEmail() : request.getEmail();

        if (!user.getUsername().equals(newUsername) && userRepository.findByUsername(newUsername).isPresent()) {
            throw new IllegalArgumentException("Ce pseudo est déjà utilisé.");
        }
        if (!user.getEmail().equals(newEmail) && userRepository.findByEmail(newEmail).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        user.setUsername(newUsername);
        user.setEmail(newEmail);

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            if (request.getOldPassword() == null ||
                    !passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new IllegalArgumentException("L'ancien mot de passe est incorrect.");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);

        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
