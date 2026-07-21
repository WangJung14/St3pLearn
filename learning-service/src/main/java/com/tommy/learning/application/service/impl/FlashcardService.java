package com.tommy.learning.application.service.impl;

import com.tommy.learning.application.dto.request.ReviewFlashcardRequest;
import com.tommy.learning.application.dto.response.DueCardResponse;
import com.tommy.learning.application.dto.response.FlashcardHistorySummaryResponse;
import com.tommy.learning.application.service.IFlashcardService;
import com.tommy.learning.domain.entity.flashcard.FlashcardProgress;
import com.tommy.learning.infrastructure.persistence.repository.FlashcardProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashcardService implements IFlashcardService {

    private final FlashcardProgressRepository progressRepository;
    private final FlashcardEngineService engineService;

    @Override
    public Page<DueCardResponse> getDueCards(UUID studentId, Pageable pageable) {
        // Query progress where nextReviewDate <= tomorrow to handle timezone and new cards
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        Page<FlashcardProgress> dueProgress = progressRepository.findDueCards(studentId, tomorrow, pageable);
        if (dueProgress.isEmpty()) {
            dueProgress = progressRepository.findByStudentId(studentId, pageable);
        }
        
        return dueProgress.map(progress -> DueCardResponse.builder()
                .flashcardId(progress.getFlashcard().getId())
                .vocabularyId(progress.getFlashcard().getVocabulary().getId())
                .frontType(progress.getFlashcard().getFrontType().name())
                .backType(progress.getFlashcard().getBackType().name())
                .lemma(progress.getFlashcard().getVocabulary().getLemma())
                .phonetic(progress.getFlashcard().getVocabulary().getPhonetic())
                .partOfSpeech(progress.getFlashcard().getVocabulary().getPartOfSpeech())
                .build());
    }

    @Override
    public void reviewCard(UUID studentId, UUID flashcardId, ReviewFlashcardRequest request) {
        engineService.reviewCard(studentId, flashcardId, request);
    }

    @Override
    public FlashcardHistorySummaryResponse getDashboardHistory(UUID studentId) {
        // Simple mock implementation for now, should query history repo in a real app
        return FlashcardHistorySummaryResponse.builder()
                .totalCardsReviewed(0)
                .newCardsLearned(0)
                .masteredCards(0)
                .averageEasinessFactor(2.5)
                .build();
    }
}
