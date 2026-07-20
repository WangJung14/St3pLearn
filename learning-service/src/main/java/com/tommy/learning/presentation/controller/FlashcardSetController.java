package com.tommy.learning.presentation.controller;

import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/learning/flashcard-sets")
@RequiredArgsConstructor
@Slf4j
public class FlashcardSetController {

    @PostMapping
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<Void>> createFlashcardSet(
            @RequestHeader("X-User-Id") UUID instructorId) {
        log.info("Instructor {} creating flashcard set", instructorId);
        // TODO: Implement create set
        return ResponseEntity.ok(ApiResponse.success(200, "Created successfully (mock)", null));
    }

    @PostMapping("/{id}/clone")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<Void>> cloneFlashcardSet(
            @RequestHeader("X-User-Id") UUID instructorId,
            @PathVariable UUID id) {
        log.info("Instructor {} cloning flashcard set {}", instructorId, id);
        // TODO: Implement clone set logic
        return ResponseEntity.ok(ApiResponse.success(200, "Cloned successfully (mock)", null));
    }
}
