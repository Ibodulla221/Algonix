package com.code.algonix.problems;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Tag(name = "Code Templates", description = "Kod template'larini boshqarish")
public class CodeTemplateController {

    private final CodeTemplateService codeTemplateService;

    @GetMapping("/problem/{problemId}")
    @Operation(summary = "Masala uchun template'lar", description = "Masala uchun barcha template'larni olish")
    public ResponseEntity<List<CodeTemplate>> getTemplatesForProblem(@PathVariable Long problemId) {
        List<CodeTemplate> templates = codeTemplateService.getTemplatesForProblem(problemId);
        return ResponseEntity.ok(templates);
    }

    @PostMapping("/admin/load")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Template'larni yuklash", description = "JSON fayldan template'larni yuklash")
    public ResponseEntity<Map<String, Object>> loadTemplates() {
        try {
            codeTemplateService.loadTemplates();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Template'lar muvaffaqiyatli yuklandi"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Xato: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/admin/problem/{problemId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Template yaratish", description = "Masala uchun yangi template yaratish")
    public ResponseEntity<CodeTemplate> createTemplate(
            @PathVariable Long problemId,
            @RequestParam String language,
            @RequestBody String code) {
        CodeTemplate template = codeTemplateService.saveTemplate(problemId, language, code);
        return ResponseEntity.ok(template);
    }
}