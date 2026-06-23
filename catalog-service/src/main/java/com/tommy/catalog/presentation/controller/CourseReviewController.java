package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.dto.request.SubmitReviewRequest;
import com.tommy.catalog.application.dto.response.ReviewResponse;
import com.tommy.catalog.application.service.ICourseReviewService;
import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseReviewController {

    private final ICourseReviewService courseReviewService;

    @PostMapping("/{courseId}/reviews")
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<ReviewResponse>> submitReview(
            @PathVariable UUID courseId,
            @RequestHeader("X-User-Id") UUID studentId,
            @Valid @RequestBody SubmitReviewRequest request) {

        log.info("Receiving review submission from Student {} for Course {}", studentId, courseId);

        ReviewResponse response = courseReviewService.submitReview(studentId, courseId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Course review submitted successfully!", response));
    }
}
