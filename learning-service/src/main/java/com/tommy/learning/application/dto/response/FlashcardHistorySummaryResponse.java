package com.tommy.learning.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlashcardHistorySummaryResponse {
    private long totalCardsReviewed;
    private long newCardsLearned;
    private long masteredCards;
    private double averageEasinessFactor;
}
