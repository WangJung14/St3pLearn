package com.tommy.catalog.application.service.serviceimpl;

import com.tommy.catalog.application.dto.request.CreateCourseRequest;
import com.tommy.catalog.application.dto.request.UpdateCourseRequest;
import com.tommy.catalog.application.service.ICourseService;
import com.tommy.catalog.domain.entity.Course;
import com.tommy.catalog.domain.enums.CourseStatus;
import com.tommy.catalog.domain.exception.AppException;
import com.tommy.catalog.domain.exception.ErrorCode;
import com.tommy.catalog.infrastructure.persistence.repository.CourseRepository;
import com.tommy.catalog.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService implements ICourseService {

    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public Course createCourse(UUID instructorId, CreateCourseRequest request) {

        // 1. Turn the Title into a basic Slug
        String baseSlug = SlugUtil.toSlug(request.getTitle());
        String uniqueSlug = baseSlug;

        // 2. The algorithm checks and assigns numbers if duplicates
        int counter = 1;
        while (courseRepository.existsBySlug(uniqueSlug)) {
            uniqueSlug = baseSlug + "-" + counter;
            counter++;
        }

        // 3. Return entity
        Course course = Course.builder()
                .instructorId(instructorId) // Get teacher id
                .title(request.getTitle())
                .slug(uniqueSlug)
                .shortDescription(request.getShortDescription())
                .level(request.getLevel())
                .language(request.getLanguage())
                .price(request.getPrice())
                .status(CourseStatus.DRAFT)
                .build();

        // 4. Save to database
        Course savedCourse = courseRepository.save(course);
        log.info("Teacher {} created new draft course: {}", instructorId, savedCourse.getSlug());

        return savedCourse;
    }

    /*
    * Update course
    * */
    @Override
    @Transactional
    public Course updateCourse(UUID courseId, UUID instructorId, UpdateCourseRequest request) {
        // 1. Find course by id
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // 2. Check id of the course creator and editor.
        if (!course.getInstructorId().equals(instructorId)) {
            throw new AppException(ErrorCode.COURSE_ACCESS_DENIED);
        }

        // 3. Update data
        course.setTitle(request.getTitle());
        course.setShortDescription(request.getShortDescription());
        course.setLevel(request.getLevel());
        course.setLanguage(request.getLanguage());
        course.setPrice(request.getPrice());

        // 4. Save to database
        Course updatedCourse = courseRepository.save(course);
        log.info("Instructor {} successfully updated course: {}", instructorId, courseId);

        return updatedCourse;
    }

    /*
    * Get course by id
    * */
    @Override
    @Transactional(readOnly = true)
    public Course getCourseById(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        return course;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Course> getAllCoursesForAdmin(int page , int size){
        // sort by creation date (createAt) in descending order
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Course> courses = courseRepository.findAll(pageable);

        return courses;
    }
}