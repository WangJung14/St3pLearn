package com.tommy.catalog.application.service;

import com.tommy.catalog.application.dto.request.LessonContentRequest;
import com.tommy.catalog.domain.entity.LessonContent;

import java.util.UUID;

public interface ILessonContentService {
    LessonContent saveContent(UUID courseId, UUID chapterId, UUID lessonId, UUID instructorId, LessonContentRequest request);
}
