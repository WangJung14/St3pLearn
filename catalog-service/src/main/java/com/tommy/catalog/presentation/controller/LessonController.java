package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.dto.request.LessonRequest;
import com.tommy.catalog.application.dto.response.ApiResponse;
import com.tommy.catalog.application.service.ICourseLessonService;
import com.tommy.catalog.domain.entity.CourseLesson;
import com.tommy.common.security.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/{courseId}/chapters/{chapterId}/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final ICourseLessonService courseLessonService;

    // Get lesson list
    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseLesson>>> getLessons(
            @PathVariable UUID courseId,
            @PathVariable UUID chapterId
            )
    {
        List<CourseLesson> lessons = courseLessonService.getLessonsByChapterId(courseId, chapterId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,"Get all lesson successful",lessons));
    }

    // Create new lesson
    @PostMapping
    @RequireRole({"ADMIN","TEACHER"})
    public ResponseEntity<ApiResponse<CourseLesson>> createLesson(
            @PathVariable UUID courseId,
            @PathVariable UUID chapterId,
            @RequestHeader("X-User-Id") UUID instructorId,
            @Valid @RequestBody LessonRequest request
    )
    {
        CourseLesson lesson = courseLessonService.createLesson(courseId, chapterId, instructorId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201,"Create lesson successful",lesson));
    }

    // Update lesson
    @PostMapping("/{lessonId}")
    @RequireRole({"ADMIN","TEACHER"})
    public ResponseEntity<ApiResponse<CourseLesson>> updateLesson(
            @PathVariable UUID courseId,
            @PathVariable UUID chapterId,
            @PathVariable UUID lessonId,
            @RequestHeader("X-User-Id") UUID instructorId,
            @Valid @RequestBody LessonRequest request
    )
    {
        CourseLesson lesson = courseLessonService.updateLesson(courseId, chapterId, lessonId, instructorId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,"Update lesson successful",lesson));
    }

    // Delete lesson
    @DeleteMapping("/{lessonId}")
    @RequireRole({"ADMIN","TEACHER"})
    public ResponseEntity<ApiResponse<CourseLesson>> deleteLesson(
            @PathVariable UUID courseId,
            @PathVariable UUID chapterId,
            @PathVariable UUID lessonId,
            @RequestHeader("X-User-Id") UUID instructorId
    )
    {
        courseLessonService.deleteLesson(courseId, chapterId, lessonId, instructorId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,"Delete lesson successful",null));
    }
}
