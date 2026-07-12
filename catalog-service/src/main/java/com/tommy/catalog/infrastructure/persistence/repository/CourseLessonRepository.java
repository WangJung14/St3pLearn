package com.tommy.catalog.infrastructure.persistence.repository;

import com.tommy.catalog.domain.entity.CourseLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseLessonRepository extends JpaRepository<CourseLesson, UUID> {

    List<CourseLesson> findByChapterIdOrderByDisplayOrderAsc(UUID chapterId);


    @Query("SELECT COALESCE(MAX(l.displayOrder), 0) FROM CourseLesson l WHERE l.chapterId = :chapterId")
    Integer findMaxDisplayOrderByChapterId(@Param("chapterId") UUID chapterId);

    // Find all chapter of course then count all lesson in this chapter
    @Query("SELECT COUNT(l) FROM CourseLesson l WHERE l.chapterId IN (SELECT c.id FROM CourseChapter c WHERE c.courseId = :courseId)")
    int countLessonsByCourseId(@Param("courseId") UUID courseId);
}