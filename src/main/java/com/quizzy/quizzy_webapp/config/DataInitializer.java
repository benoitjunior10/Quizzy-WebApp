package com.quizzy.quizzy_webapp.config;

import com.quizzy.quizzy_webapp.model.*;
import com.quizzy.quizzy_webapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * Données initiales pour démarrer rapidement.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData() {
        return args -> {
            // Admin par défaut
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .email("admin@quizzy.com")
                        .password(passwordEncoder.encode("password123"))
                        .role(Role.ROLE_ADMIN)
                        .build();
                userRepository.save(admin);
                log.info("Utilisateur admin créé: username=admin password=password123");
            }

            // Catégories + questions si la base est vide
            if (categoryRepository.count() == 0) {
                Category chimie = categoryRepository.save(Category.builder().name("Chimie").build());
                Category maths = categoryRepository.save(Category.builder().name("Maths").build());

                questionRepository.saveAll(List.of(
                        buildQuestion(chimie, "EASY", "Quelle est la formule chimique de l'eau ?",
                                "H₂O", "CO₂", "H₂O₂", "NaCl"),
                        buildQuestion(chimie, "EASY", "Quel est le symbole chimique du Fer ?",
                                "Fe", "F", "Ir", "Be"),
                        buildQuestion(chimie, "EASY", "Quel gaz est nécessaire à la combustion (feu) ?",
                                "Le dioxygène (O₂)", "Le diazote (N₂)", "L'hélium (He)", "Le dioxyde de carbone (CO₂)"),
                        buildQuestion(chimie, "EASY", "À quel pH une solution est-elle considérée comme neutre ?",
                                "7", "0", "14", "1"),
                        buildQuestion(chimie, "MEDIUM", "Quel est le nom commun du composé chimique NaCl ?",
                                "Le sel de table", "Le bicarbonate", "Le sucre", "L'eau de javel"),
                        buildQuestion(chimie, "MEDIUM", "Quel est le numéro atomique de l'Hélium ?",
                                "2", "1", "4", "10"),
                        buildQuestion(chimie, "MEDIUM", "Comment appelle-t-on une réaction chimique qui libère de la chaleur ?",
                                "Exothermique", "Endothermique", "Catalytique", "Isotherme"),
                        buildQuestion(chimie, "MEDIUM", "Quel est le constituant principal de l'air que nous respirons (environ 78%) ?",
                                "Le diazote (N₂)", "Le dioxygène (O₂)", "L'argon (Ar)", "Le méthane (CH₄)"),
                        buildQuestion(chimie, "HARD", "Quel groupement fonctionnel caractérise les alcools en chimie organique ?",
                                "Le groupement hydroxyle (-OH)", "Le groupement carboxyle (-COOH)", "Le groupement amine (-NH₂)", "Le groupement carbonyle (C=O)"),
                        buildQuestion(chimie, "HARD", "Quelle est la masse molaire approximative du dioxygène (O₂) ?",
                                "32 g/mol", "16 g/mol", "8 g/mol", "1 g/mol"),
                        buildQuestion(chimie, "HARD", "Dans la loi des gaz parfaits PV = nRT, que représente la lettre 'n' ?",
                                "Le nombre de moles", "La pression", "La constante des gaz", "Le niveau de température"),
                        buildQuestion(chimie, "HARD", "Quel chimiste est considéré comme le père de la chimie moderne pour sa loi sur la conservation de la masse ?",
                                "Antoine Lavoisier", "Dmitri Mendeleïev", "Marie Curie", "John Dalton"),

                        buildQuestion(maths, "EASY", "Combien font 7 x 8 ?",
                                "56", "54", "64", "48"),
                        buildQuestion(maths, "EASY", "Quelle est la racine carrée de 81 ?",
                                "9", "8", "7", "10"),
                        buildQuestion(maths, "EASY", "Combien de côtés possède un hexagone ?",
                                "6", "5", "8", "4"),
                        buildQuestion(maths, "EASY", "Si une pomme coûte 0,50€, combien coûtent 12 pommes ?",
                                "6,00€", "5,50€", "12,00€", "6,50€"),
                        buildQuestion(maths, "MEDIUM", "Résolvez l'équation : 2x + 5 = 15. Quelle est la valeur de x ?",
                                "5", "10", "7,5", "2"),
                        buildQuestion(maths, "MEDIUM", "Quelle est l'aire d'un triangle avec une base de 10 cm et une hauteur de 6 cm ?",
                                "30 cm²", "60 cm²", "16 cm²", "45 cm²"),
                        buildQuestion(maths, "MEDIUM", "Quel est le résultat de 1/2 + 1/4 ?",
                                "3/4", "2/6", "1/6", "2/4"),
                        buildQuestion(maths, "MEDIUM", "Quelle est la valeur de 5 au cube (5³) ?",
                                "125", "25", "75", "150"),
                        buildQuestion(maths, "HARD", "Dans un triangle rectangle, si les côtés adjacents sont 3 et 4, quelle est l'hypoténuse ?",
                                "5", "7", "6", "25"),
                        buildQuestion(maths, "HARD", "Quelle est la dérivée de f(x) = x² ?",
                                "2x", "x", "2", "x²"),
                        buildQuestion(maths, "HARD", "Quelle est la valeur approximative du nombre Pi (π) au dix-millième ?",
                                "3,1416", "3,1415", "3,1412", "3,1418"),
                        buildQuestion(maths, "HARD", "Si un dé équilibré est lancé, quelle est la probabilité d'obtenir un nombre premier ?",
                                "1/2", "1/3", "2/3", "1/6")
                ));

                log.info("Données demo créées : 2 catégories, 24 questions, 96 options.");
            }
        };
    }

    private Question buildQuestion(Category category,
                                   String difficulty,
                                   String questionText,
                                   String correctOption,
                                   String wrongOption1,
                                   String wrongOption2,
                                   String wrongOption3) {

        Question question = Question.builder()
                .questionText(questionText)
                .difficulty(difficulty)
                .category(category)
                .build();

        question.addOption(Option.builder().optionText(correctOption).correct(true).build());
        question.addOption(Option.builder().optionText(wrongOption1).correct(false).build());
        question.addOption(Option.builder().optionText(wrongOption2).correct(false).build());
        question.addOption(Option.builder().optionText(wrongOption3).correct(false).build());

        return question;
    }
}
