package com.tommy.learning.application.service;

import com.tommy.learning.application.dto.request.EnrollCourseRequest;
import com.tommy.learning.application.dto.response.EnrollmentResponse;

import java.util.UUID;

public interface IEnrollmentService {
    // Enroll in the course
    EnrollmentResponse enrollCourse(UUID studentId, EnrollCourseRequest request);
}
