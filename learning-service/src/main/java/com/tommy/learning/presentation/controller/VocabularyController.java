package com.tommy.learning.presentation.controller;

import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/learning/vocabulary")
@RequiredArgsConstructor
@Slf4j
public class VocabularyController {

    @PostMapping("/import")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<Void>> importVocabulary(
            @RequestHeader("X-User-Id") UUID instructorId,
            @RequestParam("file") MultipartFile file) {
        log.info("Instructor {} importing vocabulary from CSV", instructorId);
        // TODO: Implement CSV Parser -> Validator -> Normalizer -> DupCheck
        return ResponseEntity.ok(ApiResponse.success(200, "Imported successfully (mock)", null));
    }
}
