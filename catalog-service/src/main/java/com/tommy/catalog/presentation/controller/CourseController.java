package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.dto.request.*;
import com.tommy.catalog.application.dto.response.ApiResponse;
import com.tommy.catalog.application.dto.response.CourseApprovalDetailResponse;
import com.tommy.catalog.application.dto.response.CourseApprovalResponse;
import com.tommy.catalog.application.service.ICourseService;
import com.tommy.catalog.domain.entity.Course;
import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import com.tommy.common.security.RequireRole;
import org.springframework.web.servlet.function.EntityResponse;

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
    @RequireRole("ADMIN")
    public ResponseEntity<ApiResponse<Page<Course>>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        log.info("Admin requested all courses list with page: {}, size: {}", page, size);

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

    //Archive course ( soft delete)
    @DeleteMapping("/{courseId}/archive")
    @RequireRole({"TEACHER", "ADMIN"})
    public ResponseEntity<ApiResponse<Void>> archiveCourse(
            @PathVariable UUID courseId,
            @RequestHeader("X-User-Id") UUID instructorId) {

        courseService.archiveCourse(courseId, instructorId);

        return ResponseEntity.ok(ApiResponse.success(200, "Course successfully archived (soft deleted)", null));
    }

    // Assign category and tags for course
    @PostMapping("/{courseId}/taxonomy")
    @RequireRole({"TEACHER", "ADMIN"})
    public ResponseEntity<ApiResponse<Course>>assignTaxonomy(
            @PathVariable UUID courseId,
            @RequestHeader("X-User-Id") UUID instructorId,
            @Valid
            @RequestBody CourseTaxonomyRequest request
    ){
        log.info("Instructor {} updating categories/tags for course {}", instructorId, courseId);
        Course updatedCourse = courseService.assignCategoriesAndTags(courseId, instructorId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,"Update a successful course",updatedCourse));
    }

    // Submit course for admin approved
    @PostMapping("/{courseId}/submit")
    @RequireRole({"ADMIN","TEACHER"})
    public ResponseEntity<ApiResponse<Void>> submitForApproval(
            @PathVariable UUID courseId,
            @RequestHeader("X-User-Id") UUID instructorId) {

        courseService.submitCourseForApproval(courseId, instructorId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,"Course review request submitted successfully.",null));
    }

    // Process course approval for ADMIn
    @PostMapping("/approvals/{requestId}/process")
    @RequireRole({"ADMIN"})
    public ResponseEntity<ApiResponse<Void>> processCourseApproval(
            @PathVariable UUID requestId,
            @RequestHeader("X-User-Id") UUID adminId,
            @Valid @RequestBody ProcessApprovalRequest request) {

        courseService.processCourseApproval(requestId, adminId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "Process course request successful", null));
    }

    // Get pending course requests
    @GetMapping("/approvals/pending")
    @RequireRole({"ADMIN"})
    public ResponseEntity<ApiResponse<Page<CourseApprovalResponse>>> getPendingApprovals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<CourseApprovalResponse> pendingList = courseService.getPendingApprovals(page, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "Get the list of courses waiting for successful approval", pendingList));
    }

    // Get detail course pending
    @GetMapping("/approvals/{requestId}")
    @RequireRole({"ADMIN"})
    public ResponseEntity<ApiResponse<CourseApprovalDetailResponse>> getApprovalDetail(
            @PathVariable UUID requestId) {

        CourseApprovalDetailResponse detail = courseService.getApprovalDetail(requestId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "Get details of successful approval request", detail));
    }

    // Search publish course
    @GetMapping("/p/search")
    public ResponseEntity<ApiResponse<Page<Course>>> searchPublicCourses(
            CourseSearchRequest request) {

        Page<Course> courses = courseService.searchPublicCourses(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "Search for successful course listings", courses));
    }

    // Get course by instructor_id
    @GetMapping("/my-courses")
    @RequireRole({"TEACHER"}) // Only teacher can access
    public ResponseEntity<ApiResponse<Page<Course>>> getMyCourses(
            @RequestHeader("X-User-Id") UUID instructorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Course> myCourses = courseService.getMyCourses(instructorId, page, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "Get list of course successful", myCourses));
    }
}
