package com.tommy.catalog.application.service;

import com.tommy.catalog.application.dto.request.CourseTaxonomyRequest;
import com.tommy.catalog.application.dto.request.CreateCourseRequest;
import com.tommy.catalog.application.dto.request.UpdateCourseRequest;
import com.tommy.catalog.domain.entity.Course;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ICourseService {
    Course getCourseById(UUID courseId);
    Page<Course> getAllCoursesForAdmin(int page , int size);
    Course createCourse(UUID instructorId, CreateCourseRequest request);
    Course updateCourse(UUID courseId, UUID instructorId, UpdateCourseRequest request);
    void archiveCourse(UUID courseId, UUID instructorId);

    // Assign category and tag
    Course assignCategoriesAndTags(UUID courseId, UUID instructorId, CourseTaxonomyRequest request);

    // Submit course for admin approve
    void submitCourseForApproval(UUID courseId, UUID instructorId);
}
