package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.flashcard.FlashcardReviewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FlashcardReviewHistoryRepository extends JpaRepository<FlashcardReviewHistory, UUID> {
}
