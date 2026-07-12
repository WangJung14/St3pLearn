package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.dto.request.LessonContentRequest;
import com.tommy.catalog.application.dto.request.LessonRequest;
import com.tommy.catalog.application.dto.response.ApiResponse;
import com.tommy.catalog.application.service.ICourseLessonService;
import com.tommy.catalog.application.service.ILessonContentService;
import com.tommy.catalog.application.service.IMediaUploadService;
import com.tommy.catalog.domain.entity.CourseLesson;
import com.tommy.catalog.domain.entity.LessonContent;
import com.tommy.common.security.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/{courseId}/chapters/{chapterId}/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final ICourseLessonService courseLessonService;
    private final IMediaUploadService  mediaUploadService;
    private final ILessonContentService lessonContentService;

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

    //upload-signature
    @GetMapping("/upload-signature")
    @RequireRole({"TEACHER", "ADMIN"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUploadSignature(
            @PathVariable UUID courseId,
            @PathVariable UUID chapterId,
            @RequestHeader("X-User-Id") UUID instructorId
    ) {
        courseLessonService.validateOwnershipAndHierarchy(courseId, chapterId, instructorId);

        Map<String, Object> signatureData = mediaUploadService.generateCloudinarySignature();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,"Get upload signature successful",signatureData));
    }

    // Save lesson content
    @PostMapping("/{lessonId}/content")
    @RequireRole({"TEACHER", "ADMIN"})
    public ResponseEntity<ApiResponse<LessonContent>> saveLessonContent(
            @PathVariable UUID courseId,
            @PathVariable UUID chapterId,
            @PathVariable UUID lessonId,
            @RequestHeader("X-User-Id") UUID instructorId,
            @Valid @RequestBody LessonContentRequest request) {

        LessonContent content = lessonContentService.saveContent(courseId, chapterId, lessonId, instructorId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201,"Create lesson content successful",content));
    }
}
