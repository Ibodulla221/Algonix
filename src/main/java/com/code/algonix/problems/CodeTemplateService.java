package com.code.algonix.problems;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeTemplateService {

    private final CodeTemplateRepository codeTemplateRepository;
    private final ProblemRepository problemRepository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        loadTemplates();
    }

    public void loadTemplates() {
        try {
            ClassPathResource resource = new ClassPathResource("code-templates.json");
            
            TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<>() {};
            List<Map<String, Object>> templateData = objectMapper.readValue(resource.getInputStream(), typeRef);
            
            for (Map<String, Object> data : templateData) {
                Long problemId = Long.valueOf(data.get("problemId").toString());
                @SuppressWarnings("unchecked")
                Map<String, String> templates = (Map<String, String>) data.get("templates");
                
                Optional<Problem> problemOpt = problemRepository.findById(problemId);
                if (problemOpt.isPresent()) {
                    Problem problem = problemOpt.get();
                    
                    // Eski template'larni o'chirish
                    List<CodeTemplate> existingTemplates = codeTemplateRepository.findByProblem(problem);
                    codeTemplateRepository.deleteAll(existingTemplates);
                    log.info("Deleted {} existing templates for problem {}", existingTemplates.size(), problemId);
                    
                    // Yangi template'larni qo'shish
                    for (Map.Entry<String, String> entry : templates.entrySet()) {
                        String language = entry.getKey();
                        String code = entry.getValue();
                        
                        CodeTemplate template = CodeTemplate.builder()
                            .problem(problem)
                            .language(language)
                            .code(code)
                            .build();
                        
                        codeTemplateRepository.save(template);
                        log.debug("Saved new template for problem {} in {}", problemId, language);
                    }
                } else {
                    log.warn("Problem with ID {} not found, skipping templates", problemId);
                }
            }
            
            log.info("Code templates loaded successfully");
            
        } catch (IOException e) {
            log.error("Failed to load code templates", e);
        }
    }

    /**
     * Masala va til uchun template olish
     */
    public Optional<CodeTemplate> getTemplate(Long problemId, String language) {
        return problemRepository.findById(problemId)
            .flatMap(problem -> codeTemplateRepository.findByProblemAndLanguage(problem, language));
    }

    /**
     * Masala uchun barcha template'lar
     */
    public List<CodeTemplate> getTemplatesForProblem(Long problemId) {
        return problemRepository.findById(problemId)
            .map(codeTemplateRepository::findByProblem)
            .orElse(List.of());
    }

    /**
     * Template yaratish yoki yangilash
     */
    public CodeTemplate saveTemplate(Long problemId, String language, String code) {
        Problem problem = problemRepository.findById(problemId)
            .orElseThrow(() -> new RuntimeException("Problem not found: " + problemId));
        
        Optional<CodeTemplate> existingTemplate = codeTemplateRepository
            .findByProblemAndLanguage(problem, language);
        
        if (existingTemplate.isPresent()) {
            CodeTemplate template = existingTemplate.get();
            template.setCode(code);
            return codeTemplateRepository.save(template);
        } else {
            CodeTemplate template = CodeTemplate.builder()
                .problem(problem)
                .language(language)
                .code(code)
                .build();
            return codeTemplateRepository.save(template);
        }
    }
}