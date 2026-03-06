/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.quizzy.quizzy_webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author HP
 */
import com.quizzy.quizzy_webapp.model.*;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    // @EntityGraph dit a Hibernate de faire un JOIN sur 'options' immédiatement
   // de maniere a passer moins de requete possible
    @EntityGraph(attributePaths = {"options"}) 
    List<Question> findByCategoryId(Long categoryId);

    int countByCategoryId(Long categoryId); //compter le nombre de question par categories
}
