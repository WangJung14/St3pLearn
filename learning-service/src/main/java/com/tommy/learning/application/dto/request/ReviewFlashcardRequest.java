package com.tommy.learning.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewFlashcardRequest {
    @NotNull(message = "Quality score is required")
    @Min(value = 0, message = "Quality score must be between 0 and 5")
    @Max(value = 5, message = "Quality score must be between 0 and 5")
    private Integer qualityScore;
}
