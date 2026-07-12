package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.dto.request.ReplyReviewRequest;
import com.tommy.catalog.application.dto.request.SubmitReviewRequest;
import com.tommy.catalog.application.dto.response.ReviewReplyResponse;
import com.tommy.catalog.application.dto.response.ReviewResponse;
import com.tommy.catalog.application.service.ICourseReviewService;
import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    // create review course
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

    // View all review of course
    @GetMapping("/p/{courseId}/reviews")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getCourseReviews(
            @PathVariable UUID courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ReviewResponse> reviews = courseReviewService.getCourseReviews(courseId, PageRequest.of(page, size));

        return ResponseEntity.ok(ApiResponse.success(200, "Get the list of successful reviews", reviews));
    }

    // Update review course
    @PostMapping("/{courseId}/reviews/{reviewId}")
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable UUID courseId,
            @PathVariable UUID reviewId,
            @RequestHeader("X-User-Id") UUID studentId,
            @Valid @RequestBody SubmitReviewRequest request) {

        log.info("Student {} updating review {} for Course {}", studentId, reviewId, courseId);

        ReviewResponse response = courseReviewService.updateReview(studentId, courseId, reviewId, request);

        return ResponseEntity.ok(ApiResponse.success(200, "Update successful review", response));
    }

    // Delete review course
    @DeleteMapping("/{courseId}/reviews/{reviewId}")
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable UUID courseId,
            @PathVariable UUID reviewId,
            @RequestHeader("X-User-Id") UUID studentId) {

        log.info("Student {} deleting review {} for Course {}", studentId, reviewId, courseId);

        courseReviewService.deleteReview(studentId, courseId, reviewId);

        return ResponseEntity.ok(ApiResponse.success(200, "Delete review successfully", null));
    }

    // Teacher reply review
    @PostMapping("/{courseId}/reviews/{reviewId}/reply")
    @RequireRole({"TEACHER", "ADMIN"})
    public ResponseEntity<ApiResponse<ReviewReplyResponse>> replyToReview(
            @PathVariable UUID courseId,
            @PathVariable UUID reviewId,
            @RequestHeader("X-User-Id") UUID teacherId,
            @Valid @RequestBody ReplyReviewRequest request) {

        log.info("Instructor {} replying to review {} for Course {}", teacherId, reviewId, courseId);

        ReviewReplyResponse response = courseReviewService.replyToReview(teacherId, courseId, reviewId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Feedback on successful evaluation", response));
    }
}
