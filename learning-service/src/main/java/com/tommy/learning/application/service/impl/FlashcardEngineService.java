package com.tommy.learning.application.service.impl;

import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import com.tommy.learning.application.dto.request.ReviewFlashcardRequest;
import com.tommy.learning.domain.entity.flashcard.Flashcard;
import com.tommy.learning.domain.entity.flashcard.FlashcardProgress;
import com.tommy.learning.domain.entity.flashcard.FlashcardReviewHistory;
import com.tommy.learning.domain.enums.LearningState;
import com.tommy.learning.infrastructure.persistence.repository.FlashcardProgressRepository;
import com.tommy.learning.infrastructure.persistence.repository.FlashcardRepository;
import com.tommy.learning.infrastructure.persistence.repository.FlashcardReviewHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashcardEngineService {

    private final FlashcardProgressRepository progressRepository;
    private final FlashcardReviewHistoryRepository historyRepository;
    private final FlashcardRepository flashcardRepository;

    @Transactional
    public void reviewCard(UUID studentId, UUID flashcardId, ReviewFlashcardRequest request) {
        Flashcard flashcard = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> new AppException(ErrorCode.FLASHCARD_NOT_FOUND));

        FlashcardProgress progress = progressRepository.findByStudentIdAndFlashcardId(studentId, flashcardId)
                .orElse(FlashcardProgress.builder()
                        .studentId(studentId)
                        .flashcard(flashcard)
                        .build());

        int q = request.getQualityScore();
        float efBefore = progress.getEasinessFactor();
        int repetition = progress.getRepetition();
        int interval = progress.getIntervalDays();

        // 1. Update Repetition & Interval
        if (q < 3) {
            repetition = 0;
            interval = 1;
        } else {
            if (repetition == 0) {
                interval = 1;
            } else if (repetition == 1) {
                interval = 6;
            } else {
                interval = Math.round(interval * efBefore);
            }
            repetition++;
        }

        // 2. Update Easiness Factor (EF)
        float efNew = (float) (efBefore + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02)));
        if (efNew < 1.3f) {
            efNew = 1.3f;
        }

        // 3. Update Next Review Date (UTC standard)
        LocalDateTime nextReviewDate = LocalDateTime.now().plusDays(interval);

        // Update Learning State
        LearningState state = LearningState.LEARNING;
        if (interval > 21) {
            state = LearningState.MASTERED;
        } else if (repetition > 1) {
            state = LearningState.REVIEW;
        }

        progress.setRepetition(repetition);
        progress.setIntervalDays(interval);
        progress.setEasinessFactor(efNew);
        progress.setNextReviewDate(nextReviewDate);
        progress.setLearningState(state);

        progressRepository.save(progress);

        // Insert History
        FlashcardReviewHistory history = FlashcardReviewHistory.builder()
                .studentId(studentId)
                .flashcard(flashcard)
                .qualityScore(q)
                .efBefore(efBefore)
                .efAfter(efNew)
                .build();
        historyRepository.save(history);
    }
}
