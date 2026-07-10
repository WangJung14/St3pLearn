package com.tommy.learning.presentation.controller;

import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import com.tommy.learning.application.dto.request.UpdateProgressRequest;
import com.tommy.learning.application.dto.response.ResumeLearningResponse;
import com.tommy.learning.application.service.ILearningProgressService;
import com.tommy.learning.application.service.impl.LearningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
@Slf4j
public class LearningController {
    private final LearningService learningService;
    private final ILearningProgressService learningProgressService;

    @PostMapping("/courses/{courseId}/start")
    @RequireRole({"STUDENT", "INSTRUCTOR", "ADMIN"})
    public ResponseEntity<ApiResponse<Map<String, UUID>>> startLearning(
            @RequestHeader("X-User-Id") UUID studentId,
            @PathVariable UUID courseId) {

        log.info("Received request to start learning course {} for student {}", courseId, studentId);

        UUID lessonIdToPlay = learningService.startLearning(studentId, courseId);

        Map<String, UUID> response = new HashMap<>();
        response.put("lessonId", lessonIdToPlay);

        return ResponseEntity.ok(ApiResponse.success(200, "Learning progress initialized", response));
    }

    @PostMapping("/courses/{courseId}/lessons/{lessonId}/progress")
    @RequireRole("STUDENT")
    public ResponseEntity<ApiResponse<Void>> trackProgress(
            @RequestHeader("X-User-Id") UUID studentId,
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId,
            @Valid @RequestBody UpdateProgressRequest request) {

        log.debug("Received request to track progress for student {}, course {}, lesson {}", studentId, courseId, lessonId);

        learningProgressService.trackProgress(studentId, courseId, lessonId, request);

        return ResponseEntity.ok(ApiResponse.success(200, "Progress tracked successfully", null));
    }

    @GetMapping("/courses/{courseId}/resume")
    @RequireRole("STUDENT")
    public ResponseEntity<ApiResponse<ResumeLearningResponse>> resumeLearning(
            @RequestHeader("X-User-Id") UUID studentId,
            @PathVariable UUID courseId) {
        
        log.info("Received request to resume learning for student {}, course {}", studentId, courseId);
        
        ResumeLearningResponse response = learningProgressService.resumeLearning(studentId, courseId);
        
        return ResponseEntity.ok(ApiResponse.success(200, "Resume learning data retrieved", response));
    }
}
