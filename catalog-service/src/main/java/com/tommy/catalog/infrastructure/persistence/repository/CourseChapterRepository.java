package com.tommy.catalog.infrastructure.persistence.repository;

import com.tommy.catalog.domain.entity.CourseChapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseChapterRepository extends JpaRepository<CourseChapter, UUID> {
    // Get the chapter list arranged in the correct order
    List<CourseChapter> findByCourseIdOrderByDisplayOrderAsc(UUID courseId);

    //Get the current largest index position (if there are no chapters yet, return 0)
    @Query("SELECT COALESCE(MAX(c.displayOrder), 0) FROM CourseChapter c WHERE c.courseId = :courseId")
    Integer findMaxDisplayOrderByCourseId(@Param("courseId") UUID courseId);
}
