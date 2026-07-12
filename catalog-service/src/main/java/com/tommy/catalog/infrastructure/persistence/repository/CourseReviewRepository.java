package com.tommy.catalog.infrastructure.persistence.repository;

import com.tommy.catalog.domain.entity.Course;
import com.tommy.catalog.domain.entity.CourseReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, UUID> {

    //Check if this student has previously reviewed this course (to prevent spam).
    Optional<CourseReview> findByCourseIdAndStudentId(UUID courseId, UUID studentId);

    // Get a list of reviews sorted by time.
    Page<CourseReview> findByCourseIdOrderByCreatedAtDesc(UUID courseId, Pageable pageable);
}