package com.tommy.catalog.application.service;

import com.tommy.catalog.application.dto.request.CreateCourseRequest;
import com.tommy.catalog.application.dto.request.UpdateCourseRequest;
import com.tommy.catalog.domain.entity.Course;

import java.util.UUID;

public interface ICourseService {
    Course createCourse(UUID instructorId, CreateCourseRequest request);
    Course updateCourse(UUID courseId, UUID instructorId, UpdateCourseRequest request);
}
