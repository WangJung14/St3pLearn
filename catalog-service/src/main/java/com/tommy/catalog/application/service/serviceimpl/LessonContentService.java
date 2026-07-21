package com.tommy.catalog.application.service.serviceimpl;

import com.tommy.catalog.application.dto.request.LessonContentRequest;
import com.tommy.catalog.application.service.ILessonContentService;
import com.tommy.catalog.domain.entity.Course;
import com.tommy.catalog.domain.entity.CourseChapter;
import com.tommy.catalog.domain.entity.CourseLesson;
import com.tommy.catalog.domain.entity.LessonContent;
import com.tommy.catalog.infrastructure.persistence.repository.CourseLessonRepository;
import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import com.tommy.catalog.infrastructure.persistence.repository.CourseChapterRepository;
import com.tommy.catalog.infrastructure.persistence.repository.CourseRepository;
import com.tommy.catalog.infrastructure.persistence.repository.LessonContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonContentService implements ILessonContentService {

    private final CourseChapterRepository courseChapterRepository;
    private final CourseRepository courseRepository;
    private final LessonContentRepository contentRepository;
    private final CourseLessonRepository courseLessonRepository;

    @Override
    @Transactional
    public LessonContent saveContent(UUID courseId, UUID chapterId, UUID lessonId, UUID instructorId, LessonContentRequest request) {

        // 1. Validate logic and ownership of course
        validateOwnershipAndHierarchy(courseId, chapterId, instructorId);

        // 2. Check if this lessonId already contains any content;
        // if it does, then the action should be an Overwrite (Update)
        CourseLesson courseLesson = courseLessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        // 3. Find old content
        LessonContent content = contentRepository.findByLessonId(lessonId)
                .orElse(new LessonContent());

        content.setLesson(courseLesson);

        content.setContentType(request.getContentType());
        content.setStorageUrl(request.getStorageUrl());
        content.setFileSize(request.getFileSize());
        content.setChecksum(request.getChecksum());
        content.setMetadata(request.getMetadata());
        content.setTextContent(request.getTextContent());

        return contentRepository.save(content);
    }

    /*
    * Helped function
    * */
    //Validate logic and ownership of course
    private void validateOwnershipAndHierarchy(UUID courseId, UUID chapterId, UUID instructorId){

        // 1. Find chapter by id
        CourseChapter chapter = courseChapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        // 2. Check if the chapter is in course
        boolean isInCourse = chapter.getCourseId().equals(courseId);
        if(!isInCourse){
            throw new AppException(ErrorCode.CHAPTER_NOT_FOUND);
        }

        // 3.Check if the instructor has ownership of the course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        boolean isOwnershipCourse = course.getInstructorId().equals(instructorId);
        if(!isOwnershipCourse){
            throw new AppException(ErrorCode.FORBIDDEN_ROLE);
        }
    }


}
