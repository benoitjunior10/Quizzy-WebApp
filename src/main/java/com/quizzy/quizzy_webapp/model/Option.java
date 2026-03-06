/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quizzy.quizzy_webapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author HP
 */
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_options")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Option {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String optionText;
    @Column(name="is_correct")
    private boolean correct;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    @JsonIgnore // 👈 Ajoute ceci pour empêcher de réafficher la question dans chaque option
    private Question question;
}