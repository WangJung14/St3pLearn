package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, UUID> {
    
    long countByStudentIdAndExamId(UUID studentId, UUID examId);

    boolean existsByExamId(UUID examId);
}
