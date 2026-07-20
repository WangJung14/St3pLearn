package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.ExamSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamSubmissionRepository extends JpaRepository<ExamSubmission, UUID> {
    List<ExamSubmission> findByAttemptId(UUID attemptId);
}
