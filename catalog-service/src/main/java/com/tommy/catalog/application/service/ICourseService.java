package com.tommy.catalog.application.service;

import com.tommy.catalog.application.dto.request.*;
import com.tommy.catalog.application.dto.response.*;
import com.tommy.catalog.domain.entity.Course;
import org.springframework.data.domain.Page;

import java.util.List;
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
    Page<CourseCardResponse> searchPublicCourses(CourseSearchRequest request);

    // Get a list of courses that instructor has owner
    Page<Course> getMyCourses(UUID instructorId ,int page, int size);

    // Recall Course Approval Request
    void cancelCourseApproval(UUID courseId, UUID instructorId);

    // Publish course
    void publishCourse(UUID courseId, UUID instructorId);

    // Get public Course Detail
    CourseDetailPublicResponse getPublicCourseDetail(String slug);

    // Get course summary
    List<CourseSummaryResponse> getCourseSummaries(List<UUID> courseIds);


    // Admin remove course content for violation
    void adminRemoveCourse(UUID courseId, String reason, String adminToken);

    // Migrate course status to Learning Service
    void migrateCourseStatuses();


}
