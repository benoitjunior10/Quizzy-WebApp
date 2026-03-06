package com.quizzy.quizzy_webapp.dto;

import com.quizzy.quizzy_webapp.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @NotBlank(message = "Le pseudo est obligatoire")
    private String username;

    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    // Optionnel : si renseigné, on remplace le mot de passe
    private String password;

    private Role role;
}
