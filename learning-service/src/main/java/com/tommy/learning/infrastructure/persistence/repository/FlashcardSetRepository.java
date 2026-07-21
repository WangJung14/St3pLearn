package com.tommy.learning.infrastructure.persistence.repository;

import com.tommy.learning.domain.entity.flashcard.FlashcardSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FlashcardSetRepository extends JpaRepository<FlashcardSet, UUID> {
    Optional<FlashcardSet> findByIdAndDeletedAtIsNull(UUID id);

    List<FlashcardSet> findByInstructorIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID instructorId);

    @Query("""
            SELECT flashcardSet FROM FlashcardSet flashcardSet
            WHERE flashcardSet.deletedAt IS NULL
              AND (
                flashcardSet.visibility IN (com.tommy.learning.domain.enums.VisibilityEnum.PUBLIC, com.tommy.learning.domain.enums.VisibilityEnum.SYSTEM)
                OR (flashcardSet.visibility = com.tommy.learning.domain.enums.VisibilityEnum.COURSE_ONLY
                    AND flashcardSet.courseId IS NOT NULL
                    AND EXISTS (SELECT enrollment.id FROM Enrollment enrollment
                                WHERE enrollment.studentId = :studentId
                                  AND enrollment.courseId = flashcardSet.courseId))
              )
            ORDER BY flashcardSet.createdAt DESC
            """)
    List<FlashcardSet> findAvailableForStudent(@Param("studentId") UUID studentId);

    @Query("""
            SELECT CASE WHEN COUNT(flashcardSet) > 0 THEN true ELSE false END
            FROM FlashcardSet flashcardSet
            WHERE flashcardSet.id = :setId
              AND flashcardSet.deletedAt IS NULL
              AND (
                flashcardSet.visibility IN (com.tommy.learning.domain.enums.VisibilityEnum.PUBLIC, com.tommy.learning.domain.enums.VisibilityEnum.SYSTEM)
                OR (flashcardSet.visibility = com.tommy.learning.domain.enums.VisibilityEnum.COURSE_ONLY
                    AND flashcardSet.courseId IS NOT NULL
                    AND EXISTS (SELECT enrollment.id FROM Enrollment enrollment
                                WHERE enrollment.studentId = :studentId
                                  AND enrollment.courseId = flashcardSet.courseId))
              )
            """)
    boolean existsAvailableForStudent(@Param("studentId") UUID studentId, @Param("setId") UUID setId);
}
