package com.tommy.catalog.application.service;

import com.tommy.catalog.domain.entity.CourseChapter;

import java.util.List;
import java.util.UUID;

public interface IChapterService {
    // get course chapter list
    List<CourseChapter> getChaptersByCourseId(UUID courseId);
}
