package com.tommy.catalog.application.service;

import com.tommy.catalog.application.dto.request.LessonRequest;
import com.tommy.catalog.domain.entity.CourseLesson;

import java.util.List;
import java.util.UUID;

public interface ICourseLessonService {

    // Get all lesson of chapter
    List<CourseLesson> getLessonsByChapterId(UUID courseId, UUID chapterId);

    // Create new lesson
    CourseLesson createLesson(UUID courseId, UUID chapterId, UUID instructorId, LessonRequest request);

    // Update lesson
    CourseLesson updateLesson(UUID courseId, UUID chapterId,UUID lessonId,UUID instructorId, LessonRequest request);

    // Delete course
    void deleteLesson(UUID courseId, UUID chapterId, UUID lessonId, UUID instructorId);
}
