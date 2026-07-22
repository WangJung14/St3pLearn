package com.tommy.learning.presentation.controller;

import com.tommy.common.response.ApiResponse;
import com.tommy.learning.domain.entity.SpeakingEvaluation;
import com.tommy.learning.infrastructure.persistence.repository.SpeakingEvaluationRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/learning/speaking/evaluations")
@RequiredArgsConstructor
@Slf4j
public class SpeakingEvaluationController {

    private final SpeakingEvaluationRepository speakingEvaluationRepository;

    @Data
    public static class SaveEvaluationRequest {
        private UUID studentId;
        private UUID courseId;
        private UUID lessonId;
        private String feedback;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SpeakingEvaluation>> saveEvaluation(
            @RequestBody SaveEvaluationRequest request) {
        
        log.info("Saving speaking evaluation for student {} in lesson {}", request.getStudentId(), request.getLessonId());

        SpeakingEvaluation evaluation = SpeakingEvaluation.builder()
                .studentId(request.getStudentId())
                .courseId(request.getCourseId())
                .lessonId(request.getLessonId())
                .feedback(request.getFeedback())
                .build();

        evaluation = speakingEvaluationRepository.save(evaluation);
        return ResponseEntity.ok(ApiResponse.success(201, "Speaking evaluation saved successfully", evaluation));
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<ApiResponse<List<SpeakingEvaluation>>> getEvaluationHistory(
            @RequestHeader("X-User-Id") UUID studentId,
            @PathVariable UUID lessonId) {
        
        log.info("Fetching speaking evaluation history for student {} in lesson {}", studentId, lessonId);
        
        List<SpeakingEvaluation> history = speakingEvaluationRepository
                .findByStudentIdAndLessonIdOrderByCreatedAtDesc(studentId, lessonId);
                
        return ResponseEntity.ok(ApiResponse.success(200, "Fetched speaking evaluation history successfully", history));
    }
}
