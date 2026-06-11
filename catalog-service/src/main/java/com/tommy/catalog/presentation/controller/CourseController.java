package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.dto.request.CreateCourseRequest;
import com.tommy.catalog.application.dto.response.ApiResponse;
import com.tommy.catalog.application.dto.service.ICourseService;
import com.tommy.catalog.domain.entity.Course;
import com.tommy.catalog.domain.exception.AppException;
import com.tommy.catalog.domain.exception.ErrorCode;
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
public class CourseController {

    //DI
    private final ICourseService courseService;

    @PostMapping
    public ResponseEntity<ApiResponse<Course>> createCourse(
            @RequestHeader("X-User-Id") UUID instructorId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody CreateCourseRequest request) {

        log.info("Received request to create course from instructor: {} with role: {}", instructorId, role);

        if (!"TEACHER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
            throw new AppException(ErrorCode.FORBIDDEN_ROLE);
        }

        Course createdCourse = courseService.createCourse(instructorId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(200,"Create a successful course",createdCourse));
    }
}
