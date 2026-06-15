package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.dto.response.ApiResponse;
import com.tommy.catalog.application.service.ICourseLessonService;
import com.tommy.catalog.domain.entity.CourseLesson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses/{courseId}/chapters/{chapterId}/lessons")
@RequiredArgsConstructor
public class LessonRepository {
    private final ICourseLessonService courseLessonService;

    // Get lesson list
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
}
