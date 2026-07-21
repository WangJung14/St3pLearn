package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);

    Page<Enrollment> findByStudentId(UUID studentId, Pageable pageable);
    java.util.List<Enrollment> findByStudentId(UUID studentId);

    Optional<Enrollment> findByStudentIdAndCourseId(UUID studentId, UUID courseId);
}