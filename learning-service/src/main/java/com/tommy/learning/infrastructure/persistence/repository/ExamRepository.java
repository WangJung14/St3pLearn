package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {
    
    List<Exam> findByInstructorIdAndIsDeletedFalse(UUID instructorId);
    
    List<Exam> findByCourseIdAndIsDeletedFalse(UUID courseId);
    
    Optional<Exam> findByIdAndIsDeletedFalse(UUID id);
}
