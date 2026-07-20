package com.tommy.learning.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateExamQuestionsRequest {
    @NotNull(message = "Questions list cannot be null")
    private List<UUID> questionIds;
}
