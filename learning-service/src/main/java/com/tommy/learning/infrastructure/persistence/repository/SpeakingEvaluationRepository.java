package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.SpeakingEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpeakingEvaluationRepository extends JpaRepository<SpeakingEvaluation, UUID> {
    
    List<SpeakingEvaluation> findByStudentIdAndLessonIdOrderByCreatedAtDesc(UUID studentId, UUID lessonId);

    List<SpeakingEvaluation> findByStudentIdAndCourseIdOrderByCreatedAtDesc(UUID studentId, UUID courseId);
}
