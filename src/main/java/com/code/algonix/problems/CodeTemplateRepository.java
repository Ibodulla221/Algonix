package com.code.algonix.problems;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeTemplateRepository extends JpaRepository<CodeTemplate, Long> {
    
    /**
     * Masala va til bo'yicha template topish
     */
    Optional<CodeTemplate> findByProblemAndLanguage(Problem problem, String language);
    
    /**
     * Masala uchun barcha template'lar
     */
    List<CodeTemplate> findByProblem(Problem problem);
    
    /**
     * Til bo'yicha barcha template'lar
     */
    List<CodeTemplate> findByLanguage(String language);
}