package com.tommy.learning.presentation.controller;

import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import com.tommy.learning.application.dto.request.CreateQuestionBankRequest;
import com.tommy.learning.application.dto.request.UpdateQuestionBankRequest;
import com.tommy.learning.application.dto.response.QuestionBankResponse;
import com.tommy.learning.application.service.IQuestionBankService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/learning/question-banks")
@RequiredArgsConstructor
@Slf4j
public class QuestionBankController {

    private final IQuestionBankService questionBankService;

    @PostMapping
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<QuestionBankResponse>> createBank(
            @RequestHeader("X-User-Id") UUID instructorId,
            @Valid @RequestBody CreateQuestionBankRequest request) {

        log.info("Received request to create question bank from instructor: {}", instructorId);
        QuestionBankResponse response = questionBankService.createBank(instructorId, request);
        return ResponseEntity.ok(ApiResponse.success(201, "Question bank created successfully", response));
    }

    @PutMapping("/{bankId}")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<QuestionBankResponse>> updateBank(
            @RequestHeader("X-User-Id") UUID instructorId,
            @PathVariable UUID bankId,
            @Valid @RequestBody UpdateQuestionBankRequest request) {

        log.info("Received request to update question bank {} from instructor: {}", bankId, instructorId);
        QuestionBankResponse response = questionBankService.updateBank(instructorId, bankId, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Question bank updated successfully", response));
    }

    @DeleteMapping("/{bankId}")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<Void>> deleteBank(
            @RequestHeader("X-User-Id") UUID instructorId,
            @PathVariable UUID bankId) {

        log.info("Received request to delete question bank {} from instructor: {}", bankId, instructorId);
        questionBankService.deleteBank(instructorId, bankId);
        return ResponseEntity.ok(ApiResponse.success(200, "Question bank deleted successfully", null));
    }

    @GetMapping
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<List<QuestionBankResponse>>> getMyBanks(
            @RequestHeader("X-User-Id") UUID instructorId) {

        log.info("Received request to get question banks for instructor: {}", instructorId);
        List<QuestionBankResponse> response = questionBankService.getMyBanks(instructorId);
        return ResponseEntity.ok(ApiResponse.success(200, "Fetched question banks successfully", response));
    }
}
