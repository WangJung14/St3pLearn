package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.flashcard.FlashcardSetCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FlashcardSetCardRepository extends JpaRepository<FlashcardSetCard, UUID> {
    List<FlashcardSetCard> findByFlashcardSetIdOrderByDisplayOrderAsc(UUID setId);
    long countByFlashcardSetId(UUID setId);
}
