package com.tommy.catalog.application.service.serviceimpl;

import com.tommy.catalog.application.service.IChapterService;
import com.tommy.catalog.domain.entity.CourseChapter;
import com.tommy.catalog.infrastructure.persistence.repository.CourseChapterRepository;
import com.tommy.catalog.infrastructure.persistence.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChapterService implements IChapterService {

    private final CourseChapterRepository courseChapterRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourseChapter> getChaptersByCourseId(UUID courseId) {
        List<CourseChapter> courseChapters = courseChapterRepository.findByCourseIdOrderByDisplayOrderAsc(courseId);
        return courseChapters;
    }

}
