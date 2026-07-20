package com.tommy.learning.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class QuestionAnswerRequest {
    @NotNull(message = "Question ID is required")
    private UUID questionId;
    private List<String> selectedOptionIds;
    private String textAnswer;
    private String audioUrl;
}
