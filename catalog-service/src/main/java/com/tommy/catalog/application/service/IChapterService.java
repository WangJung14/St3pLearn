package com.tommy.catalog.application.service;

import com.tommy.catalog.application.dto.request.ChapterRequest;
import com.tommy.catalog.domain.entity.CourseChapter;

import java.util.List;
import java.util.UUID;

public interface IChapterService {
    // get course chapter list
    List<CourseChapter> getChaptersByCourseId(UUID courseId);

    // create new chapter
    CourseChapter createChapter(UUID courseId, UUID instructorId, ChapterRequest request);

    // update chapter
    CourseChapter updateChapter(UUID courseId, UUID chapterId, UUID instructorId, ChapterRequest request);
}
