package com.tommy.learning.presentation.controller;

import com.tommy.learning.application.dto.request.EnrollCourseRequest;
import com.tommy.learning.application.dto.response.EnrollmentResponse;
import com.tommy.learning.application.service.IEnrollmentService;
import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Slf4j
public class EnrollmentController {
    private final IEnrollmentService enrollmentService;

    @PostMapping
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollCourse(
            @RequestHeader("X-User-Id") UUID studentId,
            @Valid @RequestBody EnrollCourseRequest request) {

        log.info("Received enrollment request from Student {} for Course {}", studentId, request.getCourseId());

        EnrollmentResponse response = enrollmentService.enrollCourse(studentId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Course registration successful.!", response));
    }
}
