package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.flashcard.FlashcardProgress;
import com.tommy.learning.domain.entity.flashcard.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FlashcardProgressRepository extends JpaRepository<FlashcardProgress, UUID> {
    Optional<FlashcardProgress> findByStudentIdAndFlashcardId(UUID studentId, UUID flashcardId);

    @Query("SELECT fp FROM FlashcardProgress fp WHERE fp.studentId = :studentId AND fp.nextReviewDate <= :today")
    Page<FlashcardProgress> findDueCards(@Param("studentId") UUID studentId, @Param("today") LocalDateTime today, Pageable pageable);

    @Query(value = """
            SELECT setCard.flashcard FROM FlashcardSetCard setCard
            LEFT JOIN FlashcardProgress progress
              ON progress.flashcard = setCard.flashcard AND progress.studentId = :studentId
            WHERE setCard.flashcardSet.id = :setId
              AND (progress.id IS NULL OR progress.nextReviewDate <= :today)
            ORDER BY setCard.displayOrder ASC
            """,
            countQuery = """
            SELECT COUNT(setCard) FROM FlashcardSetCard setCard
            LEFT JOIN FlashcardProgress progress
              ON progress.flashcard = setCard.flashcard AND progress.studentId = :studentId
            WHERE setCard.flashcardSet.id = :setId
              AND (progress.id IS NULL OR progress.nextReviewDate <= :today)
            """)
    Page<Flashcard> findDueCardsInSet(
            @Param("studentId") UUID studentId,
            @Param("setId") UUID setId,
            @Param("today") LocalDateTime today,
            Pageable pageable);
}
