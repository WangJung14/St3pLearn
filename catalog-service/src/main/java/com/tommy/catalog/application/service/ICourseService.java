package com.tommy.catalog.application.service;

import com.tommy.catalog.application.dto.request.*;
import com.tommy.catalog.application.dto.response.CourseApprovalDetailResponse;
import com.tommy.catalog.application.dto.response.CourseApprovalResponse;
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

    // Process Course Approval for ADMIN
    void processCourseApproval(UUID requestId, UUID adminId, ProcessApprovalRequest request);

    // Get All pending course
    Page<CourseApprovalResponse> getPendingApprovals(int page, int size);

    // Get course details
    CourseApprovalDetailResponse getApprovalDetail(UUID requestId);

    //Search course
    Page<Course> searchPublicCourses(CourseSearchRequest request);

    // Get a list of courses that instructor has owner
    Page<Course> getMyCourses(UUID instructorId ,int page, int size);

    // Recall Course Approval Request
    void cancelCourseApproval(UUID courseId, UUID instructorId);
}
