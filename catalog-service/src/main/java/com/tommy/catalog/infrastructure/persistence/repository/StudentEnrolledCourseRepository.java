package com.tommy.catalog.infrastructure.persistence.repository;

import com.tommy.catalog.domain.entity.StudentEnrolledCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudentEnrolledCourseRepository extends JpaRepository<StudentEnrolledCourse, UUID> {

    // Block students who haven't purchased the course but dare to leave reviews.
    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);
}