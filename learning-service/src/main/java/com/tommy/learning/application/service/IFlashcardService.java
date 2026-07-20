package com.tommy.learning.application.service;

import com.tommy.learning.application.dto.request.ReviewFlashcardRequest;
import com.tommy.learning.application.dto.response.DueCardResponse;
import com.tommy.learning.application.dto.response.FlashcardHistorySummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IFlashcardService {
    Page<DueCardResponse> getDueCards(UUID studentId, Pageable pageable);
    void reviewCard(UUID studentId, UUID flashcardId, ReviewFlashcardRequest request);
    FlashcardHistorySummaryResponse getDashboardHistory(UUID studentId);
}
