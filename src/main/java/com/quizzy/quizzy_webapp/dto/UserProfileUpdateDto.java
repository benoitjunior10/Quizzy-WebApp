package com.quizzy.quizzy_webapp.dto;
import lombok.Data;
@Data
public class UserProfileUpdateDto {
    private String username;
    private String email;
    private String oldPassword;
    private String newPassword;
}
