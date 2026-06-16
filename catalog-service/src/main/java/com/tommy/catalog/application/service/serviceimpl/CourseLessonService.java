package com.tommy.catalog.application.service.serviceimpl;

import com.tommy.catalog.application.dto.request.LessonRequest;
import com.tommy.catalog.application.service.ICourseLessonService;
import com.tommy.catalog.application.service.ICourseService;
import com.tommy.catalog.domain.entity.Course;
import com.tommy.catalog.domain.entity.CourseChapter;
import com.tommy.catalog.domain.entity.CourseLesson;
import com.tommy.catalog.domain.exception.AppException;
import com.tommy.catalog.domain.exception.ErrorCode;
import com.tommy.catalog.infrastructure.persistence.repository.CourseChapterRepository;
import com.tommy.catalog.infrastructure.persistence.repository.CourseLessonRepository;
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
public class CourseLessonService implements ICourseLessonService {

    //DI
    private final CourseLessonRepository courseLessonRepository;
    private final CourseChapterRepository courseChapterRepository;
    private final CourseRepository courseRepository;
    /*
    * Get all lesson of chapter
    * PUBLIC ACCESS
    * */
    @Override
    @Transactional(readOnly = true)
    public List<CourseLesson> getLessonsByChapterId(UUID courseId, UUID chapterId) {

        CourseChapter chapter = courseChapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        // Check if chapter is in the course
        boolean isInCourse = chapter.getCourseId().equals(courseId);
        if(!isInCourse){
            throw new AppException(ErrorCode.CHAPTER_NOT_FOUND);
        }

        return courseLessonRepository.findByChapterIdOrderByDisplayOrderAsc(chapterId);
    }

    /*
    * Create new chapter
    * */
    @Override
    @Transactional
    public CourseLesson createLesson(UUID courseId , UUID chapterId, UUID instructorId, LessonRequest request){
        // Validate logic and ownership of course
        validateOwnershipAndHierarchy(courseId, chapterId, instructorId);

        int nextOrder = courseLessonRepository.findMaxDisplayOrderByChapterId(chapterId) + 1;

        CourseLesson lesson = CourseLesson.builder()
                .chapterId(chapterId)
                .title(request.getTitle())
                .lessonType(request.getLessonType())
                .durationSeconds(request.getDurationSeconds() != null ? request.getDurationSeconds() : 0)
                .isPreview(request.getIsPreview() != null ? request.getIsPreview() : false)
                .displayOrder(nextOrder)
                .build();

        CourseLesson saved = courseLessonRepository.save(lesson);
        return saved;
    }

    @Override
    @Transactional
    public CourseLesson updateLesson(UUID courseId , UUID chapterId, UUID lessonId, UUID instructorId, LessonRequest request){

        // validate logic and ownership of course
        validateOwnershipAndHierarchy(courseId, chapterId, instructorId);

        // find lesson by id
        CourseLesson lesson = courseLessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        // Check if lesson is in chapter
        boolean isInChapter = lesson.getChapterId().equals(chapterId);
        if(!isInChapter){
            throw new AppException(ErrorCode.LESSON_NOT_FOUND);
        }

        lesson.setTitle(request.getTitle());
        lesson.setLessonType(request.getLessonType());

        if (request.getDurationSeconds() != null) lesson.setDurationSeconds(request.getDurationSeconds());
        if (request.getIsPreview() != null) lesson.setIsPreview(request.getIsPreview());

        CourseLesson saved =  courseLessonRepository.save(lesson);
        return saved;
    }

    /*
    * Delete lesson
    * */
    @Override
    @Transactional
    public void deleteLesson(UUID courseId, UUID chapterId, UUID lessonId, UUID instructorId) {
        // validate logic and ownership of course
        validateOwnershipAndHierarchy(courseId, chapterId, instructorId);

        // find lesson by id
        CourseLesson lesson = courseLessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        boolean isInChapter = lesson.getChapterId().equals(chapterId);
        if(!isInChapter){
            throw new AppException(ErrorCode.LESSON_NOT_FOUND);
        }

        // delete lesson from database
        courseLessonRepository.delete(lesson);
        log.info("Delete lesson with id {} ", lessonId);
    }

    /*
    * Helped function
    * */
    // Validate logic and ownership of course

    @Override
    public void validateOwnershipAndHierarchy(UUID courseId, UUID chapterId, UUID instructorId){

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
