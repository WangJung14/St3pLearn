package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.QuestionBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, UUID> {
    
    Optional<QuestionBank> findByIdAndIsDeletedFalse(UUID id);

    List<QuestionBank> findByInstructorIdAndIsDeletedFalse(UUID instructorId);

    List<QuestionBank> findByCourseIdAndIsDeletedFalse(UUID courseId);
}
