package com.tommy.learning.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class DueCardResponse {
    private UUID flashcardId;
    private UUID vocabularyId;
    private String frontType;
    private String backType;
    private String lemma;
    private String phonetic;
    private String partOfSpeech;
}
