package com.tommy.catalog.application.service.serviceimpl;

import com.tommy.catalog.application.dto.request.ChapterRequest;
import com.tommy.catalog.application.service.IChapterService;
import com.tommy.catalog.domain.entity.Course;
import com.tommy.catalog.domain.entity.CourseChapter;
import com.tommy.catalog.domain.exception.AppException;
import com.tommy.catalog.domain.exception.ErrorCode;
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

    private final CourseChapterRepository chapterRepository;
    private final CourseRepository courseRepository;

    /*
    * Get all chapter in course
    * PUBLIC ACCESS
    * */
    @Override
    @Transactional(readOnly = true)
    public List<CourseChapter> getChaptersByCourseId(UUID courseId) {
        List<CourseChapter> courseChapters = chapterRepository.findByCourseIdOrderByDisplayOrderAsc(courseId);
        return courseChapters;
    }

    /*
    * Create new chapter in course
    * */

    @Override
    @Transactional
    public CourseChapter createChapter(UUID courseId, UUID instructorId, ChapterRequest request){
        // 1.Validate course owner
        validateCourseOwnership(courseId,instructorId);

        // 2. Auto calculate display_order = max + 1
        int nextOrder = chapterRepository.findMaxDisplayOrderByCourseId(courseId) + 1;

        CourseChapter chapter = CourseChapter.builder()
                .courseId(courseId)
                .title(request.getTitle())
                .displayOrder(nextOrder)
                .build();
        return chapterRepository.save(chapter);
    }


    /*
        helped function
    */
    // validate course ownership
    private void validateCourseOwnership(UUID courseId, UUID instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getInstructorId().equals(instructorId)) {
            log.warn("Security Alert: User {} tried to modify course {}", instructorId, courseId);
            throw new AppException(ErrorCode.FORBIDDEN_ROLE);
        }
    }



}
