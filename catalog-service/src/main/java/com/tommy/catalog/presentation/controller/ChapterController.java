package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.dto.request.ChapterRequest;
import com.tommy.catalog.application.service.IChapterService;
import com.tommy.catalog.domain.entity.CourseChapter;
import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
            @RequestMapping("/api/courses/{courseId}/chapters")
@RequiredArgsConstructor
public class ChapterController {

    //DI
    private final IChapterService chapterService;

    // Get all chapter of course
    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseChapter>>> getChapters(@PathVariable UUID courseId) {

        List<CourseChapter> courseChapters = chapterService.getChaptersByCourseId(courseId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,"Get all chapters in the course successfully",courseChapters));
    }

    // Create new chapter
    @PostMapping
    @RequireRole({"TEACHER", "ADMIN"})
    public ResponseEntity<ApiResponse<CourseChapter>> createChapter(
            @PathVariable UUID courseId,
            @RequestHeader("X-User-Id") UUID instructorId,
            @Valid @RequestBody ChapterRequest request) {

        CourseChapter chapter = chapterService.createChapter(courseId, instructorId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201,"Create new chapter successful ",chapter));
    }

    // Update chapter
    @PostMapping("/{chapterId}")
    @RequireRole({"ADMIN","TEACHER"})
    public ResponseEntity<ApiResponse<CourseChapter>> updateChapter(
            @PathVariable UUID courseId,
            @PathVariable UUID chapterId,
            @RequestHeader("X-User-Id") UUID instructorId,
            @Valid @RequestBody ChapterRequest request) {

        CourseChapter chapter = chapterService.updateChapter(courseId, chapterId, instructorId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,"Update chapter successful ",chapter));
    }
}
