package com.tommy.catalog.infrastructure.persistence.repository;

import com.tommy.catalog.domain.entity.CourseWishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface CourseWishlistRepository extends JpaRepository<CourseWishlist, UUID> {

    Optional<CourseWishlist> findByStudentIdAndCourseId(UUID studentId, UUID courseId);
    Page<CourseWishlist> findByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(UUID studentId, Pageable pageable);
}
