package com.tommy.catalog.application.dto.service.serviceimpl;

import com.tommy.catalog.application.dto.request.CreateCourseRequest;
import com.tommy.catalog.application.dto.service.ICourseService;
import com.tommy.catalog.domain.entity.Course;
import com.tommy.catalog.domain.enums.CourseStatus;
import com.tommy.catalog.infrastructure.persistence.repository.CourseRepository;
import com.tommy.catalog.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}