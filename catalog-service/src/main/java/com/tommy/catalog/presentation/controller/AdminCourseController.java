package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.dto.request.AdminRemoveCourseRequest;
import com.tommy.catalog.application.dto.response.ApiResponse;
import com.tommy.catalog.application.service.ICourseService;
import com.tommy.common.security.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
@Slf4j
public class AdminCourseController {

    private final ICourseService courseService;

    // Admin remove course content for violation
    @RequireRole("ADMIN")
    @PostMapping("/{courseId}/remove")
    public ResponseEntity<ApiResponse<Void>> removeCourse(
            @PathVariable UUID courseId,
            @RequestHeader(value = "Authorization", required = false) String adminToken,
            @Valid @RequestBody AdminRemoveCourseRequest request) {

        log.info("Admin requested to remove course {} with reason: {}", courseId, request.getReason());

        courseService.adminRemoveCourse(courseId, request.getReason(), adminToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "Course removed successfully and notification sent to instructor", null));
    }
}
