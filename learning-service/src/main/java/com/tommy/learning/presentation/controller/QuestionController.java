package com.tommy.learning.presentation.controller;

import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import com.tommy.learning.application.dto.request.CreateQuestionRequest;
import com.tommy.learning.application.dto.request.UpdateQuestionRequest;
import com.tommy.learning.application.dto.response.QuestionResponse;
import com.tommy.learning.application.service.IQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
@Slf4j
public class QuestionController {

    private final IQuestionService questionService;

    @PostMapping("/question-banks/{bankId}/questions")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(
            @RequestHeader("X-User-Id") UUID instructorId,
            @PathVariable UUID bankId,
            @Valid @RequestBody CreateQuestionRequest request) {

        log.info("Received request to create question in bank {} from instructor: {}", bankId, instructorId);
        QuestionResponse response = questionService.createQuestion(instructorId, bankId, request);
        return ResponseEntity.ok(ApiResponse.success(201, "Question created successfully", response));
    }

    @PutMapping("/questions/{questionId}")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(
            @RequestHeader("X-User-Id") UUID instructorId,
            @PathVariable UUID questionId,
            @Valid @RequestBody UpdateQuestionRequest request) {

        log.info("Received request to update question {} from instructor: {}", questionId, instructorId);
        QuestionResponse response = questionService.updateQuestion(instructorId, questionId, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Question updated successfully", response));
    }

    @DeleteMapping("/questions/{questionId}")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @RequestHeader("X-User-Id") UUID instructorId,
            @PathVariable UUID questionId) {

        log.info("Received request to delete question {} from instructor: {}", questionId, instructorId);
        questionService.deleteQuestion(instructorId, questionId);
        return ResponseEntity.ok(ApiResponse.success(200, "Question deleted successfully", null));
    }

    @GetMapping("/question-banks/{bankId}/questions")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getQuestionsByBankId(
            @RequestHeader("X-User-Id") UUID instructorId,
            @PathVariable UUID bankId) {

        log.info("Received request to get questions for bank {} from instructor: {}", bankId, instructorId);
        List<QuestionResponse> response = questionService.getQuestionsByBankId(instructorId, bankId);
        return ResponseEntity.ok(ApiResponse.success(200, "Fetched questions successfully", response));
    }
}
