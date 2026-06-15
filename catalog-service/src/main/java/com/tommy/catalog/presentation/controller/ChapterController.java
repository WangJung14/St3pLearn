package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.service.IChapterService;
import com.tommy.catalog.domain.entity.CourseChapter;
import com.tommy.catalog.infrastructure.persistence.repository.CourseChapterRepository;
import com.tommy.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
