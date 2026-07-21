package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.dto.request.CommentRequest;
import com.tommy.catalog.application.dto.response.CommentResponse;
import com.tommy.catalog.application.service.ILessonCommentService;
import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/lessons")
@RequiredArgsConstructor
@Slf4j
public class LessonCommentController {

    private final ILessonCommentService commentService;

    // Get all comments of a lesson
    @GetMapping("/{lessonId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getLessonComments(@PathVariable UUID lessonId) {
        log.info("Receiving request to fetch comments for lesson {}", lessonId);
        List<CommentResponse> comments = commentService.getCommentsByLesson(lessonId);
        return ResponseEntity.ok(ApiResponse.success(200, "Get lesson comments successfully", comments));
    }

    // Create a new comment/reply
    @PostMapping("/{lessonId}/comments")
    @RequireRole({"STUDENT", "TEACHER", "ADMIN"})
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable UUID lessonId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody CommentRequest request) {

        log.info("User {} creating comment for lesson {}", userId, lessonId);
        CommentResponse response = commentService.createComment(
                lessonId,
                userId,
                request.getUserFullName(),
                userRole,
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Comment created successfully", response));
    }

    // Edit a comment
    @PutMapping("/comments/{commentId}")
    @RequireRole({"STUDENT", "TEACHER", "ADMIN"})
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable UUID commentId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody CommentRequest request) {

        log.info("User {} updating comment {}", userId, commentId);
        CommentResponse response = commentService.updateComment(commentId, userId, userRole, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Comment updated successfully", response));
    }

    // Delete a comment
    @DeleteMapping("/comments/{commentId}")
    @RequireRole({"STUDENT", "TEACHER", "ADMIN"})
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable UUID commentId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole) {

        log.info("User {} deleting comment {}", userId, commentId);
        commentService.deleteComment(commentId, userId, userRole);
        return ResponseEntity.ok(ApiResponse.success(200, "Comment deleted successfully", null));
    }
}
