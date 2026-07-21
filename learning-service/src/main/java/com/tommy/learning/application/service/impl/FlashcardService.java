package com.tommy.learning.application.service.impl;

import com.tommy.learning.application.dto.request.ReviewFlashcardRequest;
import com.tommy.learning.application.dto.response.DueCardResponse;
import com.tommy.learning.application.dto.response.FlashcardHistorySummaryResponse;
import com.tommy.learning.application.service.IFlashcardService;
import com.tommy.learning.application.service.IFlashcardSetService;
import com.tommy.learning.domain.entity.flashcard.Flashcard;
import com.tommy.learning.domain.entity.flashcard.FlashcardProgress;
import com.tommy.learning.infrastructure.persistence.repository.FlashcardProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashcardService implements IFlashcardService {

    private final FlashcardProgressRepository progressRepository;
    private final FlashcardEngineService engineService;
    private final IFlashcardSetService flashcardSetService;

    @Override
    @Transactional(readOnly = true)
    public Page<DueCardResponse> getDueCards(UUID studentId, UUID setId, Pageable pageable) {
        if (!flashcardSetService.canStudentAccess(studentId, setId)) {
            throw new com.tommy.common.exception.AppException(com.tommy.common.exception.ErrorCode.COURSE_ACCESS_DENIED);
        }
        LocalDateTime today = LocalDateTime.now(ZoneOffset.UTC);
        Page<Flashcard> dueCards = progressRepository.findDueCardsInSet(studentId, setId, today, pageable);
        
        return dueCards.map(card -> DueCardResponse.builder()
                .flashcardId(card.getId())
                .vocabularyId(card.getVocabulary().getId())
                .frontType(card.getFrontType().name())
                .backType(card.getBackType().name())
                .lemma(card.getVocabulary().getLemma())
                .phonetic(card.getVocabulary().getPhonetic())
                .partOfSpeech(card.getVocabulary().getPartOfSpeech())
                .definition(card.getVocabulary().getMeanings().isEmpty()
                        ? null
                        : card.getVocabulary().getMeanings().get(0).getDefinition())
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
