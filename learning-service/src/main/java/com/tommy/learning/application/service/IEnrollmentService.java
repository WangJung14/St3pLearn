package com.tommy.learning.application.service;

import com.tommy.learning.application.dto.request.EnrollCourseRequest;
import com.tommy.learning.application.dto.response.EnrollmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IEnrollmentService {
    // Enroll in the course
    EnrollmentResponse enrollCourse(UUID studentId, EnrollCourseRequest request);

    // View course enrollment
    Page<EnrollmentResponse> getMyEnrolledCourses(UUID studentId, int page , int size);
}
