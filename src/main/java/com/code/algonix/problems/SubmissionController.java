package com.code.algonix.problems;

import com.code.algonix.problems.SubmitRequest;
import com.code.algonix.problems.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody SubmitRequest req) {
        try {
            var res = submissionService.submit(req.getUserId(), req.getProblemId(), req.getLanguage(), req.getCode());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
