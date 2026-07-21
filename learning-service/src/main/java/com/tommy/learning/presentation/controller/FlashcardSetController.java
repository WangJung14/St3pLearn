package com.tommy.learning.presentation.controller;

import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import com.tommy.learning.application.dto.request.CreateFlashcardSetRequest;
import com.tommy.learning.application.dto.request.AddFlashcardToSetRequest;
import com.tommy.learning.application.dto.response.FlashcardSetSummaryResponse;
import com.tommy.learning.application.service.IFlashcardSetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/learning/flashcard-sets")
@RequiredArgsConstructor
@Slf4j
public class FlashcardSetController {
    private final IFlashcardSetService flashcardSetService;

    @PostMapping
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<FlashcardSetSummaryResponse>> createFlashcardSet(
            @RequestHeader("X-User-Id") UUID instructorId,
            @Valid @RequestBody CreateFlashcardSetRequest request) {
        return ResponseEntity.ok(ApiResponse.success(201, "Created successfully", flashcardSetService.create(instructorId, request)));
    }

    @GetMapping("/my-sets")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<List<FlashcardSetSummaryResponse>>> getMySets(
            @RequestHeader("X-User-Id") UUID instructorId) {
        return ResponseEntity.ok(ApiResponse.success(200, "Fetched instructor sets", flashcardSetService.getMySets(instructorId)));
    }

    @GetMapping("/available")
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<List<FlashcardSetSummaryResponse>>> getAvailableSets(
            @RequestHeader("X-User-Id") UUID studentId) {
        return ResponseEntity.ok(ApiResponse.success(200, "Fetched available sets", flashcardSetService.getAvailableSets(studentId)));
    }

    @PostMapping("/{id}/cards")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<FlashcardSetSummaryResponse>> addCard(
            @RequestHeader("X-User-Id") UUID instructorId,
            @PathVariable UUID id,
            @Valid @RequestBody AddFlashcardToSetRequest request) {
        return ResponseEntity.ok(ApiResponse.success(201, "Card added successfully", flashcardSetService.addCard(instructorId, id, request)));
    }

    @PostMapping("/{id}/clone")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<FlashcardSetSummaryResponse>> cloneFlashcardSet(
            @RequestHeader("X-User-Id") UUID instructorId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(201, "Cloned successfully", flashcardSetService.cloneSet(instructorId, id)));
    }
}
