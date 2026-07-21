package com.tommy.learning.application.dto.request;

import com.tommy.learning.domain.enums.CefrLevel;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddFlashcardToSetRequest {
    @NotBlank(message = "Word is required")
    private String lemma;

    private String language = "EN";
    private String phonetic;
    private String partOfSpeech;
    private CefrLevel cefrLevel = CefrLevel.UNKNOWN;

    @NotBlank(message = "Definition is required")
    private String definition;
}
