package com.code.algonix.config;

import com.code.algonix.problems.ProblemRepository;
import com.code.algonix.problems.ProblemService;
import com.code.algonix.problems.dto.CreateProblemRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2) // Run after DataInitializer
public class ProblemDataLoader implements CommandLineRunner {

    private final ProblemService problemService;
    private final ProblemRepository problemRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) {
        log.info("Loading problems from JSON files...");
        
        loadProblemsFromFile("problems-beginner.json");
        loadProblemsFromFile("problems-basic.json");
        loadProblemsFromFile("problems-normal.json");
        loadProblemsFromFile("problems-medium.json");
        loadProblemsFromFile("problems-hard.json");
        
        // Assign global sequence numbers after all problems are loaded
        assignGlobalSequenceNumbers();
        
        log.info("✔ All problems loaded successfully!");
    }

    private void loadProblemsFromFile(String filename) {
        try {
            ClassPathResource resource = new ClassPathResource(filename);
            InputStream inputStream = resource.getInputStream();
            
            List<CreateProblemRequest> problems = objectMapper.readValue(
                inputStream, 
                new TypeReference<List<CreateProblemRequest>>() {}
            );
            
            for (CreateProblemRequest problem : problems) {
                try {
                    problemService.createProblem(problem);
                    log.info("✔ Loaded problem: {}", problem.getTitle());
                } catch (Exception e) {
                    log.error("✗ Failed to load problem: {} - {}", problem.getTitle(), e.getMessage());
                }
            }
            
        } catch (IOException e) {
            log.error("✗ Failed to load problems from {}: {}", filename, e.getMessage());
        }
    }
    
    private void assignGlobalSequenceNumbers() {
        log.info("Assigning global sequence numbers...");
        
        // Get all problems ordered by ID
        List<com.code.algonix.problems.Problem> allProblems = problemRepository.findAll()
                .stream()
                .sorted((p1, p2) -> p1.getId().compareTo(p2.getId()))
                .toList();
        
        // Assign sequence numbers starting from 1
        for (int i = 0; i < allProblems.size(); i++) {
            com.code.algonix.problems.Problem problem = allProblems.get(i);
            problem.setGlobalSequenceNumber(i + 1);
            problemRepository.save(problem);
        }
        
        log.info("✔ Assigned global sequence numbers to {} problems", allProblems.size());
    }
}