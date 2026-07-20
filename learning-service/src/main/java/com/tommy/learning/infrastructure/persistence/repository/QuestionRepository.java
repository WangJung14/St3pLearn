package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    
    List<Question> findByBankIdAndIsDeletedFalse(UUID bankId);
    
    Optional<Question> findByIdAndIsDeletedFalse(UUID id);
}
