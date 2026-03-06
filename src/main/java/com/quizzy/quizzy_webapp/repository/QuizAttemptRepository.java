package com.quizzy.quizzy_webapp.repository;
import com.quizzy.quizzy_webapp.model.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByUserUsernameOrderByDateDesc(String username);

    // Recupere la moyenne de tous les scores
    @Query("SELECT AVG(q.score) FROM QuizAttempt q")
    Double getGlobalAverageScore();
}
