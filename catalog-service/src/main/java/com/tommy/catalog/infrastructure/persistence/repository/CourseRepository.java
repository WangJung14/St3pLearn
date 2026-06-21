package com.tommy.catalog.infrastructure.persistence.repository;

import com.tommy.catalog.domain.entity.Course;
import com.tommy.catalog.domain.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> , JpaSpecificationExecutor<Course> {
    boolean existsBySlug(String slug);

    // Get list of course by Instruction ID
    Page<Course> findByInstructorId(UUID instructorId, Pageable pageable);

    // Find course by slug and status
    Optional<Course> findBySlugAndStatus(String slug, CourseStatus status);
}
