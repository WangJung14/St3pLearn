package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, UUID> {

    List<ExamQuestion> findByExamIdOrderByDisplayOrderAsc(UUID examId);

    void deleteByExamId(UUID examId);

    boolean existsByQuestionIdAndExam_Status(UUID questionId, com.tommy.learning.domain.enums.ExamStatus status);

    long countByExamId(UUID examId);
}
