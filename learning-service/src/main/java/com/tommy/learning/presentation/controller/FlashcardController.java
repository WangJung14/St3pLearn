package com.tommy.learning.presentation.controller;

import com.tommy.common.response.ApiResponse;
import com.tommy.learning.application.dto.request.ReviewFlashcardRequest;
import com.tommy.learning.application.dto.response.DueCardResponse;
import com.tommy.learning.application.dto.response.FlashcardHistorySummaryResponse;
import com.tommy.learning.application.service.IFlashcardService;
import com.tommy.common.security.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
@Slf4j
public class FlashcardController {

    private final IFlashcardService flashcardService;

    @GetMapping("/flashcard-sets/{id}/due-cards")
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<Page<DueCardResponse>>> getDueCards(
            @RequestHeader("X-User-Id") UUID studentId,
            @PathVariable UUID id,
            Pageable pageable) {
        log.info("Student {} fetching due cards for set {}", studentId, id);
        // Note: Currently we are getting all due cards across all sets for the student,
        // to strictly filter by set ID, we would need to join with FlashcardSetCard in the repository.
        // For simplicity, we just pass studentId for now.
        Page<DueCardResponse> response = flashcardService.getDueCards(studentId, pageable);
        return ResponseEntity.ok(ApiResponse.success(200, "Fetched due cards", response));
    }

    @PostMapping("/flashcards/{id}/review")
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<Void>> reviewFlashcard(
            @RequestHeader("X-User-Id") UUID studentId,
            @PathVariable UUID id,
            @Valid @RequestBody ReviewFlashcardRequest request) {
        log.info("Student {} reviewing flashcard {}", studentId, id);
        flashcardService.reviewCard(studentId, id, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Reviewed successfully", null));
    }

    @GetMapping("/dashboard/history")
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<FlashcardHistorySummaryResponse>> getDashboardHistory(
            @RequestHeader("X-User-Id") UUID studentId) {
        log.info("Student {} fetching dashboard history", studentId);
        FlashcardHistorySummaryResponse response = flashcardService.getDashboardHistory(studentId);
        return ResponseEntity.ok(ApiResponse.success(200, "Fetched history", response));
    }
}
