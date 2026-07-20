package com.tommy.learning.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class QuestionGradeRequest {
    @NotNull(message = "Question ID is required")
    private UUID questionId;
    
    @NotNull(message = "Points awarded is required")
    private Double pointsAwarded;
    
    private String feedbackText;
}
