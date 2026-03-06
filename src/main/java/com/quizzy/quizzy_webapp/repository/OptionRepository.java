/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.quizzy.quizzy_webapp.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.quizzy.quizzy_webapp.model.*;

/**
 *
 * @author HP
 */
public interface OptionRepository extends JpaRepository<Option, Long> {}
