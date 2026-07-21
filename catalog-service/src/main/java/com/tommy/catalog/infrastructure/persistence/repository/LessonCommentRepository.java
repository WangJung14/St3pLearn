package com.tommy.catalog.infrastructure.persistence.repository;

import com.tommy.catalog.domain.entity.LessonComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonCommentRepository extends JpaRepository<LessonComment, UUID> {
    List<LessonComment> findByLessonIdOrderByCreatedAtAsc(UUID lessonId);
    List<LessonComment> findByParentCommentIdOrderByCreatedAtAsc(UUID parentCommentId);
}
