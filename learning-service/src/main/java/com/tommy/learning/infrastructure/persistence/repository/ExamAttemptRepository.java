package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.tommy.learning.domain.enums.ExamAttemptStatus;

import java.util.UUID;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, UUID> {
    
    long countByStudentIdAndExamId(UUID studentId, UUID examId);

    boolean existsByExamId(UUID examId);

    Page<ExamAttempt> findByExamIdAndStatus(UUID examId, ExamAttemptStatus status, Pageable pageable);
    
    Page<ExamAttempt> findByExamId(UUID examId, Pageable pageable);
}
