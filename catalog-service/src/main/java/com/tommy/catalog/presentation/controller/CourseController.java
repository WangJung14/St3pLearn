package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.dto.request.CreateCourseRequest;
import com.tommy.catalog.application.dto.request.UpdateCourseRequest;
import com.tommy.catalog.application.dto.response.ApiResponse;
import com.tommy.catalog.application.service.ICourseService;
import com.tommy.catalog.domain.entity.Course;
import com.tommy.catalog.domain.exception.AppException;
import com.tommy.catalog.domain.exception.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    //DI
    private final ICourseService courseService;

    // Create new course
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

    // update course by id
    @PostMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Course>> updateCourse(
            @PathVariable UUID courseId,
            @RequestHeader("X-User-Id") UUID instructorId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody UpdateCourseRequest request) {

        log.info("Received request to update course {} from instructor: {}", courseId, instructorId);

        // Check role
        if (!"TEACHER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
            throw new AppException(ErrorCode.FORBIDDEN_ROLE);
        }

        Course updatedCourse = courseService.updateCourse(courseId, instructorId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,"Update a successful course",updatedCourse));
    }

    // get all course for admin
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Course>>> getAllCourses(
            @RequestHeader("X-User-Role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size

    ){
        log.info("Admin requested all courses list with page: {}, size: {}", page, size);

        // check role , if not admin return
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        if(!isAdmin) {
            throw new AppException(ErrorCode.FORBIDDEN_ROLE);
        }

        Page<Course> coursePage = courseService.getAllCoursesForAdmin(page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,"Get All courses successful",coursePage));
    }

    // Get course by id (Public Access)
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Course>> getCourse(@PathVariable UUID courseId){
        Course course = courseService.getCourseById(courseId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,"Get course successful",course));
    }

}
